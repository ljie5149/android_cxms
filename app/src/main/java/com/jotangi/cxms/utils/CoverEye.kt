package com.jotangi.cxms.utils

import android.text.method.PasswordTransformationMethod
import android.view.View


class CoverEye : PasswordTransformationMethod() {

    override fun getTransformation(
        source: CharSequence,
        view: View?
    ): CharSequence = PasswordCharSequence(source)

    private inner class PasswordCharSequence(
        private val source: CharSequence
    ) : CharSequence {

        override val length: Int
            get() = source.length

        override fun get(index: Int): Char =
            if (index in 4..6) '•' else source[index]

        override fun subSequence(
            startIndex: Int,
            endIndex: Int
        ): CharSequence = source.subSequence(startIndex, endIndex)
    }
}