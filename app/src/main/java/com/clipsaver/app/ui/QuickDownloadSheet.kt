package com.clipsaver.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipsaver.app.AudioFormat
import com.clipsaver.app.DownloadViewModel
import com.clipsaver.app.Mode
import com.clipsaver.app.Stage
import com.clipsaver.app.VideoQuality

private val SheetHeaderCrimson = Color(0xFFB33939)
private val SheetBodyCharcoal = Color(0xFF232227)
private val SheetRowSurface = Color(0xFF2D2C32)
private val SheetOutline = Color(0xFF47454F)

@Composable
fun QuickDownloadSheet(
    vm: DownloadViewModel,
    onDismiss: () -> Unit,
    onStartDownload: () -> Unit
) {
    val s by vm.state.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDismiss)
                .padding(horizontal = 14.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SheetBodyCharcoal)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .clickable(enabled = false, onClick = {})
            ) {
                // شريط علوي مستوحى من نافذة التنزيل المنبثقة
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SheetHeaderCrimson)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "إغلاق",
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "تنزيل",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val canConfirm = s.stage == Stage.READY && s.info != null
                    Text(
                        text = "حسناً",
                        color = if (canConfirm) Color.White else Color.White.copy(alpha = 0.45f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(enabled = canConfirm, onClick = onStartDownload)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (s.engineError != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Red.copy(alpha = 0.14f))
                                .padding(10.dp)
                        ) {
                            Icon(Icons.Rounded.ErrorOutline, null, tint = Red, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(s.engineError!!, color = Color.White, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    if (s.stage == Stage.LOADING || !s.engineReady && s.info == null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 22.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = Color.White
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = if (!s.engineReady) "جاري تجهيز محرك التنزيل..." else "جاري جلب معلومات المقطع...",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        s.error?.let { err ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Red.copy(alpha = 0.14f))
                                    .padding(10.dp)
                            ) {
                                Icon(Icons.Rounded.ErrorOutline, null, tint = Red, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(err, color = Color.White, fontSize = 13.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                        }

                        // حقل اسم الملف (مثل نافذة الصور المرسلة)
                        Text(
                            text = "اسم الملف",
                            color = TextDim,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = s.customFileName,
                            onValueChange = vm::onCustomFileNameChange,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 15.sp,
                                textDirection = TextDirection.Rtl
                            ),
                            trailingIcon = {
                                if (s.customFileName.isNotEmpty()) {
                                    IconButton(onClick = { vm.onCustomFileNameChange("") }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "مسح",
                                            tint = TextDim,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = SheetOutline,
                                cursorColor = Color.White
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        // اختيار الصيغة / النوع: الصوت أو الفيديو (بأزرار راديو واضحة)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            RadioChoiceRow(
                                label = "الصوت",
                                icon = Icons.Rounded.Audiotrack,
                                selected = s.mode == Mode.AUDIO,
                                onClick = { vm.setMode(Mode.AUDIO) }
                            )
                            RadioChoiceRow(
                                label = "الفيديو",
                                icon = Icons.Rounded.VideoFile,
                                selected = s.mode == Mode.VIDEO,
                                onClick = { vm.setMode(Mode.VIDEO) }
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = SheetOutline.copy(alpha = 0.6f))
                        Spacer(Modifier.height(14.dp))

                        if (s.mode == Mode.VIDEO) {
                            Text(
                                text = "الجودة / الدقة",
                                color = TextDim,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                VideoQuality.entries.forEach { q ->
                                    val selected = s.quality == q
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (selected) SheetHeaderCrimson else SheetRowSurface)
                                            .border(
                                                1.dp,
                                                if (selected) Color.White.copy(alpha = 0.5f) else SheetOutline,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { vm.setQuality(q) }
                                            .padding(horizontal = 14.dp, vertical = 9.dp)
                                    ) {
                                        Text(
                                            text = q.label,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "صيغة الصوت",
                                color = TextDim,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AudioFormat.entries.forEach { fmt ->
                                    val selected = s.audioFormat == fmt
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (selected) SheetHeaderCrimson else SheetRowSurface)
                                            .border(
                                                1.dp,
                                                if (selected) Color.White.copy(alpha = 0.5f) else SheetOutline,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { vm.setAudioFormat(fmt) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = fmt.label,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        s.info?.let { info ->
                            Spacer(Modifier.height(14.dp))
                            val subInfo = listOfNotNull(
                                info.uploader.takeIf { it.isNotBlank() },
                                info.durationSec.takeIf { it > 0 }?.let { formatSheetDuration(it) }
                            ).joinToString(" • ")
                            if (subInfo.isNotEmpty()) {
                                Text(
                                    text = subInfo,
                                    color = TextDim,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        // شريط أزرار سفلي يطابق إحساس النافذة المرجعية
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "إلغاء",
                                color = TextDim,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable(onClick = onDismiss)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            val canConfirm = s.stage == Stage.READY && s.info != null
                            Text(
                                text = "تنزيل",
                                color = if (canConfirm) Color.White else Color.White.copy(alpha = 0.4f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (canConfirm) SheetHeaderCrimson else SheetRowSurface)
                                    .clickable(enabled = canConfirm, onClick = onStartDownload)
                                    .padding(horizontal = 18.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RadioChoiceRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) Color.White else TextDim, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun formatSheetDuration(sec: Long): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}
