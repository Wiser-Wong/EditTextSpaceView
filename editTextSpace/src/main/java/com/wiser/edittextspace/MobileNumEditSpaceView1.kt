package com.wiser.edittextspace

import android.content.Context
import android.text.*
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.lang.StringBuilder
import java.util.regex.Pattern

class MobileNumEditSpaceView1(context: Context, attrs: AttributeSet?) :
    AppCompatEditText(context, attrs) {

    private var lastTextLength: Int = 0

    private var onEditTextInputListener: OnEditTextInputListener? = null

    init {

        filters = arrayOf(InputFilter.LengthFilter(13))
        inputType = InputType.TYPE_CLASS_NUMBER
        keyListener = DigitsKeyListener.getInstance("0123456789 ");

        val sb = StringBuilder()

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sb.clear()
                sb.append(s?.toString())

                val newSb = newSb(sb)
                if (newSb.toString() != s.toString()) {
                    s?.replace(0, s.length, newSb)
                }

                lastTextLength = s?.length ?: 0

                onEditTextInputListener?.afterTextChanged(s)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onEditTextInputListener?.beforeTextChanged(p0, p1, p2, p3)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onEditTextInputListener?.onTextChanged(p0, p1, p2, p3)
            }
        })
    }

    private fun newSb(s: StringBuilder): StringBuilder {
        val ss = s.toString().replace(" ", "")
        s.clear()
        s.append(ss)
        if (s.length > 7) {
            s.insert(3, " ")
            s.insert(8, " ")
        }
        // 000 1111
        if (s.length in 4..7) {
            s.insert(3, " ")
        }

        return s
    }

    /**
     * 获取真实手机号
     */
    fun getRealMobileNumText(): String = trimPattern(text.toString())

    /**
     * 获取带空格模板的手机号
     */
    fun getFormatMobileNumText(): String = text.toString()

    fun realLength(): Int = getRealMobileNumText().length

    fun formatLength(): Int = getFormatMobileNumText().length

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

    fun addEditTextInputListener(onEditTextInputListener: OnEditTextInputListener?) {
        this.onEditTextInputListener = onEditTextInputListener
    }

}