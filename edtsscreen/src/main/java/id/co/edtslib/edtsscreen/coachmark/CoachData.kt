package id.co.edtslib.edtsscreen.coachmark

import android.graphics.Rect

data class CoachData(
    val rect: Rect,
    val imageResId: Int,
    val title: String,
    val description: String,
    val sort: Int,
    val alignInfo: CoachAlign,
    val positiveText: String,
    val trianglePosition: Int?,
    val shape: CoachShape
)