package com.wiser.edittextspace

import android.content.Context
import android.text.*
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.lang.StringBuilder
import java.util.regex.Pattern

/**
 * @author Wiser
 *
 * 手机号输入格式化130 1234 4567
 */
class MobileNumEditSpaceView(context: Context, attrs: AttributeSet?) :
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
                s?.let {
                    if (it.isNotEmpty()) {
                        // 光标位置在最后一位时，光标从最后一个字符开始删除时，如果遇到空格则删除空格
                        // 例如：000 9此时我删除9的时候，就变成000 空格，所以为了变成000没有空格，所以删除最后一个空格字符
                        if (it.last() == ' ') {
                            it.delete(it.length - 1, it.length)
                        }
                    }

                    //光标位置不在最后一位时 及在光标输入的文字内容中间进行删除增加的情况
                    //selectionEnd是光标位置，不等于内容长度，说明没有处于最后位置
                    if (selectionEnd != s.length) {
                        //s.length和lastTextLength进行比较，如果大于则增加符号
                        //当光标处于第4or9位置时，也就是空格处时要删除空格
                        //例如：000 1234 5555 光标在1后面，此时要删除1，则变成了000 234 5555,为了后面处理所以需要变成000234 5555所以需要删除空格
                        if (s.length < lastTextLength && (selectionEnd == 4 || selectionEnd == 9)) {
                            sb.delete(selectionEnd - 1, selectionEnd)
                            if (selectionEnd == 9)
                                setSelection(selectionEnd - 1)
                        }
                        // 根据光标位置格式化光标后面的字符格式
                        // 例如：上面提到的000234 5555 需要变成000 2345 55 该方法就是处理这种格式化的
                        val newSb = newSb(selectionEnd, sb)
                        // 为了不无限循环设置内容所以做了两次对比，如果不同则执行替换所有内容，否则不执行
                        if (s.toString() != newSb.toString().trim())
                            s.replace(0, s.length, newSb)
                    }
                    //s.length和lastTextLength进行比较，如果小于则减少符号
                    //当光标处于第4or9位置时，需要增加空格
                    if (s.length > lastTextLength && selectionEnd != s.length) {
                        if ((selectionEnd == 4 || selectionEnd == 9))
                            s.insert(selectionEnd - 1, " ")
                        //判断增加符号时，如果光标后面的是空格的时候，需要将光标移到空格后面
                        //例如：124 5678 999 此时我想在2后面增加字符变成123| 5678 9999 如果不处理光标就变成前面那个样子，所以将光标移到了空格后面变成123 |4567 8999
                        if (s.substring(selectionEnd, selectionEnd + 1) == " ") {
                            setSelection(selectionEnd + 1)
                        }
                    }
                }
                // 光标位置在最后一位时
                // 当输入的情况下，到第4或者第9个字符时增加空格
                // 例如：0001 应该变成000 1  or  000 11112 应该变成000 1111 2
                if (selectionEnd == s?.length && (s.length == 4 || s.length == 9)) {
                    s.insert(s.length - 1, " ")
                }

                // 记录上一次输入长度
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

    private fun newSb(selectionEnd: Int, s: StringBuilder): StringBuilder {
        val startSb = StringBuilder(s.substring(0, selectionEnd))
        val endSb = StringBuilder(s.substring(selectionEnd, s.length).replace(" ", ""))
        println(startSb)
        println(endSb)
        if (endSb.isNotEmpty()) {
            if (selectionEnd in 5..8 && endSb.length >= 4 - (selectionEnd - 4))
                endSb.insert(4 - (selectionEnd - 4), " ")
            if (selectionEnd in 0..3 && endSb.length >= 3 - selectionEnd) {
                if (endSb.length >= 4 + (3 - selectionEnd)) {
                    endSb.insert(4 + (3 - selectionEnd), " ")
                }
                endSb.insert(3 - selectionEnd, " ")
            }
        }
        return startSb.append(endSb)
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

interface OnEditTextInputListener {
    fun afterTextChanged(s: Editable?)
    fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
    fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
}