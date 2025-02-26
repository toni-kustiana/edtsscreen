package id.co.edtslib.edtsscreen.coachmark

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import id.co.edtslib.edtsscreen.R
import id.co.edtslib.edtsscreen.databinding.ViewCoachMarkBinding

open class CoachMarkView: FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var delegate: CoachMarkDelegate? = null

    protected lateinit var activity: FragmentActivity
    protected var selectedIndex = 0
        set(value) {
            field = value

            val index = list.indexOfFirst { it.sort == selectedIndex }
            if (index >= 0) {
                draw(index)
            }
        }
    protected val binding = ViewCoachMarkBinding.inflate(LayoutInflater.from(context), this, true)
    protected var contentLeftMargin = 0

    protected val list = mutableListOf<CoachData>()

    init {
        if (! isInEditMode) {
            isVisible = false
        }

        val lpTriangle = binding.llContent.layoutParams as ConstraintLayout.LayoutParams
        contentLeftMargin = lpTriangle.leftMargin

        binding.ivCancel.setOnClickListener {
            isVisible = false
            delegate?.onClose()
        }

        binding.bvPositive.setOnClickListener {
            if (selectedIndex+1 < list.size) {
                selectedIndex += 1
            }
            else {
                binding.ivCancel.performClick()
            }
        }

        binding.bvNegative.setOnClickListener {
            selectedIndex -= 1
        }
    }

    fun add(coachData: CoachData) {
        val index = list.indexOfFirst { it.sort == coachData.sort }
        if (index >= 0) {
            list[index] = coachData
        }
        else {
            list.add(coachData)
        }
    }

    fun show(activity: FragmentActivity) {
        this.activity = activity
        if (list.isNotEmpty()) {
            selectedIndex = 0
        }
    }

    protected open fun draw(index: Int) {
        isVisible = true

        val coachData = list[index]

        binding.ivTriangle.isVisible = coachData.alignInfo == CoachAlign.Bottom
        binding.ivTriangle180.isVisible = coachData.alignInfo == CoachAlign.Top

        binding.bvNegative.isVisible = coachData.sort > 0
        binding.bvPositive.text = coachData.positiveText

        val rectangle = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rectangle)

        val lpShape = binding.vShape.layoutParams as ConstraintLayout.LayoutParams
        lpShape.width = coachData.rect.width()
        lpShape.height = coachData.rect.height()
        lpShape.topMargin = coachData.rect.top - rectangle.top
        lpShape.marginStart = coachData.rect.left

        binding.vShape.layoutParams = lpShape

        val lpContent = binding.llContent.layoutParams as ConstraintLayout.LayoutParams
        lpContent.topToTop = if (coachData.alignInfo == CoachAlign.Top) ConstraintLayout.LayoutParams.UNSET else
            ConstraintLayout.LayoutParams.PARENT_ID
        lpContent.bottomToTop = if (coachData.alignInfo == CoachAlign.Top) R.id.vShape else
            ConstraintLayout.LayoutParams.UNSET


        lpContent.topMargin = if (coachData.alignInfo == CoachAlign.Top) 0 else
            lpShape.topMargin + coachData.rect.height() + resources.getDimensionPixelSize(R.dimen.dimen_coach_mark_margin)
        lpContent.bottomMargin = if (coachData.alignInfo == CoachAlign.Top) resources.getDimensionPixelSize(
            R.dimen.dimen_coach_mark_margin) else
            0

        binding.llContent.layoutParams = lpContent

        binding.ivTriangle.post {
            val lpTriangle = binding.ivTriangle.layoutParams as LinearLayout.LayoutParams
            lpTriangle.marginStart =
                (coachData.trianglePosition
                    ?: (lpShape.marginStart + lpShape.width / 2)) - contentLeftMargin -
                        binding.ivTriangle.width/2

            binding.ivTriangle.layoutParams = lpTriangle
        }

        binding.ivTriangle180.post {
            val lpTriangle180 = binding.ivTriangle180.layoutParams as LinearLayout.LayoutParams
            lpTriangle180.marginStart =
                (coachData.trianglePosition ?: (lpShape.marginStart + lpShape.width / 2)) -
                        contentLeftMargin - binding.ivTriangle180.width/2

            binding.ivTriangle180.layoutParams = lpTriangle180
        }

        binding.tvTitle.text = coachData.title
        binding.tvDescription.text = HtmlCompat.fromHtml(
            coachData.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.imageView.setImageResource(coachData.imageResId)

        binding.tvCount.text = context.getString(
            R.string.coach_mark_n_from,
            coachData.sort + 1, list.size
        )

        binding.vShape.shape = coachData.shape
    }

}