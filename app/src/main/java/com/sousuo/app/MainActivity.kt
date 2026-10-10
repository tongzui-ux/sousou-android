package com.sousuo.app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.*
import android.graphics.Typeface
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {
    private val bg = Color.rgb(16, 16, 16)
    private val panel = Color.rgb(29, 29, 29)
    private val red = Color.rgb(229, 57, 69)
    private val white = Color.rgb(245, 245, 245)
    private val muted = Color.rgb(165, 165, 170)
    private lateinit var input: EditText
    private lateinit var categoryRow: LinearLayout
    private var category = "全部"
    private var engine = "DuckDuckGo"
    private var translateToChinese = true
    private val categories = listOf("全部", "商品", "新闻", "网站", "图片")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        buildUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(28), dp(20), dp(18))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val brandRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val mark = TextView(this).apply {
            text = "⌕"
            textSize = 34f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(red, 16)
        }
        brandRow.addView(mark, LinearLayout.LayoutParams(dp(54), dp(54)))
        val brandTexts = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        brandTexts.addView(TextView(this).apply {
            text = "搜索"
            textSize = 23f
            letterSpacing = 0.08f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
        })
        brandTexts.addView(TextView(this).apply {
            text = "全球公开信息 · 一处搜索"
            textSize = 13f
            setTextColor(muted)
        })
        brandRow.addView(brandTexts, LinearLayout.LayoutParams(0, -2, 1f))
        content.addView(brandRow)
        addSpace(content, 28)

        input = EditText(this).apply {
            hint = "搜索全球商品、事件、网站…"
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 16f
            singleLine = true
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = rounded(panel, 14, Color.rgb(57, 57, 57))
            setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_search, 0, 0, 0)
            compoundDrawablePadding = dp(10)
        }
        content.addView(input, LinearLayout.LayoutParams(-1, -2))
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { hideKeyboard(); search(); true } else false
        }
        addSpace(content, 12)

        categoryRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        content.addView(categoryRow)
        renderCategories()
        addSpace(content, 16)

        val searchButton = TextView(this).apply {
            text = "开始搜索  →"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(red, 14)
            setPadding(dp(12), dp(15), dp(12), dp(15))
            setOnClickListener { hideKeyboard(); search() }
        }
        content.addView(searchButton, LinearLayout.LayoutParams(-1, -2))
        addSpace(content, 26)

        sectionTitle(content, "探索类别", "按主题搜索公开网络信息")
        addSpace(content, 12)
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        grid.addView(categoryCardRow(
            Triple("🛍", "全球交易", "商品、价格、二手交易与商家"),
            Triple("◉", "世界动态", "国际新闻、突发事件与趋势")
        ))
        addSpace(grid, 10)
        grid.addView(categoryCardRow(
            Triple("◎", "网站探索", "网站、资料和公开页面"),
            Triple("☆", "图片搜索", "探索公开图片结果")
        ))
        content.addView(grid)
        addSpace(content, 24)

        sectionTitle(content, "搜索设置", "可切换公开搜索引擎")
        addSpace(content, 10)
        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("DuckDuckGo", "Bing", "Google"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) { engine = adapter.getItem(position) ?: "DuckDuckGo" }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        content.addView(spinner, LinearLayout.LayoutParams(-1, dp(48)))
        addSpace(content, 8)
        val translateToggle = Switch(this).apply {
            text = "尝试将搜索页面翻译为简体中文"
            setTextColor(white)
            textSize = 14f
            isChecked = true
            setOnCheckedChangeListener { _, checked -> translateToChinese = checked }
        }
        content.addView(translateToggle, LinearLayout.LayoutParams(-1, dp(48)))
        addSpace(content, 12)
        content.addView(TextView(this).apply {
            text = "提示：应用本身在手机本地运行，全球搜索需要网络连接。搜索结果由所选公开搜索引擎提供；本应用不内置 VPN 或加速器。"
            textSize = 12f
            setTextColor(muted)
            setLineSpacing(dp(3).toFloat(), 1f)
        })

        setContentView(root)
    }

    private fun renderCategories() {
        categoryRow.removeAllViews()
        categories.forEachIndexed { index, item ->
            val chip = TextView(this).apply {
                text = item
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(if (category == item) Color.WHITE else muted)
                background = rounded(if (category == item) red else panel, 10)
                setPadding(dp(14), dp(10), dp(14), dp(10))
                setOnClickListener { category = item; renderCategories() }
            }
            val params = LinearLayout.LayoutParams(-2, -2)
            if (index > 0) params.leftMargin = dp(8)
            categoryRow.addView(chip, params)
        }
    }

    private fun categoryCardRow(a: Triple<String, String, String>, b: Triple<String, String, String>): LinearLayout {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(categoryCard(a.first, a.second, a.third), LinearLayout.LayoutParams(0, dp(116), 1f))
        val spacer = View(this)
        row.addView(spacer, LinearLayout.LayoutParams(dp(10), 1))
        row.addView(categoryCard(b.first, b.second, b.third), LinearLayout.LayoutParams(0, dp(116), 1f))
        return row
    }

    private fun categoryCard(icon: String, title: String, desc: String): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(12), dp(10))
            background = rounded(panel, 14)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                category = when (title) {
                    "全球交易" -> "商品"
                    "世界动态" -> "新闻"
                    "网站探索" -> "网站"
                    else -> "图片"
                }
                renderCategories()
                input.requestFocus()
            }
        }
        card.addView(TextView(this).apply { text = icon; textSize = 20f; setTextColor(red) })
        card.addView(TextView(this).apply { text = title; textSize = 15f; typeface = Typeface.DEFAULT_BOLD; setTextColor(white) })
        card.addView(TextView(this).apply {
            text = desc; textSize = 11f; setTextColor(muted); maxLines = 2; ellipsize = TextUtils.TruncateAt.END
        })
        return card
    }

    private fun sectionTitle(parent: LinearLayout, title: String, subtitle: String) {
        parent.addView(TextView(this).apply { text = title; textSize = 18f; typeface = Typeface.DEFAULT_BOLD; setTextColor(white) })
        parent.addView(TextView(this).apply { text = subtitle; textSize = 12f; setTextColor(muted) })
    }

    private fun search() {
        val q = input.text.toString().trim()
        if (q.isEmpty()) {
            Toast.makeText(this, "请先输入搜索内容", Toast.LENGTH_SHORT).show()
            input.requestFocus()
            return
        }
        val encoded = Uri.encode(q)
        val url = when (category) {
            "商品" -> when (engine) {
                "Bing" -> "https://www.bing.com/shop?q=$encoded"
                "Google" -> "https://www.google.com/search?tbm=shop&q=$encoded"
                else -> "https://duckduckgo.com/?q=$encoded+shopping"
            }
            "新闻" -> "https://news.google.com/search?q=$encoded"
            "图片" -> when (engine) {
                "Bing" -> "https://www.bing.com/images/search?q=$encoded"
                "Google" -> "https://www.google.com/search?tbm=isch&q=$encoded"
                else -> "https://duckduckgo.com/?q=$encoded&iax=images&ia=images"
            }
            "网站" -> when (engine) {
                "Bing" -> "https://www.bing.com/search?q=$encoded"
                "Google" -> "https://www.google.com/search?q=$encoded"
                else -> "https://html.duckduckgo.com/html/?q=$encoded"
            }
            else -> when (engine) {
                "Bing" -> "https://www.bing.com/search?q=$encoded"
                "Google" -> "https://www.google.com/search?q=$encoded"
                else -> "https://duckduckgo.com/?q=$encoded"
            }
        }
        val finalUrl = if (translateToChinese) {
            "https://translate.google.com/translate?sl=auto&tl=zh-CN&u=${Uri.encode(url)}"
        } else url
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)))
        } catch (_: Exception) {
            Toast.makeText(this, "无法打开浏览器，请检查手机是否安装浏览器", Toast.LENGTH_LONG).show()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }

    private fun addSpace(parent: LinearLayout, heightDp: Int) {
        parent.addView(View(this), LinearLayout.LayoutParams(1, dp(heightDp)))
    }

    private fun rounded(color: Int, radiusDp: Int, stroke: Int? = null): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
            if (stroke != null) setStroke(dp(1), stroke)
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
