package com.wiser.edittextspace

import android.content.Context
import android.text.*
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.util.regex.Pattern

/**
 * @author Wiser
 *
 *      手机号输入格式化130 1234 4567
 */
class MobileNumEditSpaceView(context: Context, attrs: AttributeSet?) :
    AppCompatEditText(context, attrs) {

    private var lastTextLength: Long? = 0

    init {

        filters = arrayOf(InputFilter.LengthFilter(13))
        inputType = InputType.TYPE_CLASS_NUMBER
        keyListener = DigitsKeyListener.getInstance("0123456789 ");

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty() && it.last() == ' ') {
                        it.delete(it.length - 1, it.length)
                    }
                }
                s?.length?.let { t ->
                    lastTextLength?.let { l ->
                        if (t.toLong() < l) {
                            lastTextLength = t.toLong()
                            return
                        }
                    }
                }
                if (s?.length == 4 || s?.length == 9) {
                    s.insert(s.length - 1, " ")
                }
                s?.length?.let {
                    lastTextLength = it.toLong()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    /**
     * 获取真实手机号
     */
    fun getRealMobileNumText(): String = trimPattern(text.toString())

    /**
     * 获取带空格模板的手机号
     */
    fun getFormatMobileNumText(): String = text.toString()

    /**
     * 去掉空格
     * @param s
     * @return
     */
    private fun trimPattern(s: CharSequence): String {
        if (TextUtils.isEmpty(s)) return ""
        val p = Pattern.compile("\\s")
        val m = p.matcher(s)
        return m.replaceAll("");
    }
}