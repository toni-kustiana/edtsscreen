package id.co.edtslib.edtsscreen.coachmark

data class CoachShape(
    val type: CoachShapeType,
    val radius: Int?
) {
    companion object {
        fun createRectangle(rad: Int) =
            CoachShape(
                type = CoachShapeType.Rectangle,
                radius = rad
            )

        fun createCircle() = CoachShape(
            type = CoachShapeType.Circle,
            radius = null
        )

    }
}