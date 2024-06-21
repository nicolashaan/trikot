package com.mirego.trikot.viewmodels.declarative.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.mirego.trikot.viewmodels.declarative.components.VMDTextViewModel
import com.mirego.trikot.viewmodels.declarative.configuration.TrikotViewModelDeclarative
import com.mirego.trikot.viewmodels.declarative.properties.VMDRichTextSpan
import com.mirego.trikot.viewmodels.declarative.properties.VMDSpanStyleResourceTransform
import com.mirego.trikot.viewmodels.declarative.properties.VMDTextStyleResource

@Composable
fun VMDTextStyleResource.textStyle(): TextStyle? =
    TrikotViewModelDeclarative.textStyleProvider.textStyleForResource(this)

@Composable
fun VMDTextViewModel.toAnnotatedString() = buildAnnotatedString {
    append(text)
    spans.forEach { addSpanStyle(it) }
}

@Composable
private fun AnnotatedString.Builder.addSpanStyle(spanStyle: VMDRichTextSpan) {
    spanStyle.toComposeSpanStyle()?.let {
        addStyle(
            style = it,
            start = spanStyle.range.first,
            end = spanStyle.range.last
        )
    }
}

@Composable
private fun VMDRichTextSpan.toComposeSpanStyle(): SpanStyle? =
    when (val currentTransform = transform) {
        is VMDSpanStyleResourceTransform -> currentTransform.textStyleResource.textStyle()?.toSpanStyle()
        else -> null
    }
