package id.co.edtsscreen

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.co.edtslib.edtsscreen.coachmark.CoachAlign
import id.co.edtslib.edtsscreen.coachmark.CoachData
import id.co.edtslib.edtsscreen.coachmark.CoachNavigationType
import id.co.edtslib.edtsscreen.coachmark.CoachShape
import id.co.edtsscreen.databinding.ActivityCoachmarkBinding

class CoachmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoachmarkBinding

    companion object{
        fun open(activity: AppCompatActivity){
            activity.startActivity(
                Intent(
                    activity,
                    CoachmarkActivity::class.java
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoachmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val coachMarkView = binding.coachMarkView
        coachMarkView.navigationType = CoachNavigationType.Bullet
        coachMarkView.canSkip = true
        coachMarkView.canBack = false
        coachMarkView.navigationType = CoachNavigationType.None


        coachMarkView.postDelayed({
            val rect10 = Rect()
            binding.tv10.getGlobalVisibleRect(rect10)

            val coachData10 = CoachData(
                rect = rect10,
                imageResId = R.drawable.ic_coach_mark_1,
                title = "Title Coachmark 10",
                description = "Deskripsi Coachmark 10 Deskripsi Coachmark 10 Deskripsi Coachmark 10 Deskripsi Coachmark 10",
                sort = 0,
                alignInfo = CoachAlign.Bottom,
                positiveText = "Selanjutnya",
                trianglePosition = null,
                shape = CoachShape.createRectangle(
                    rad = resources.getDimensionPixelSize(id.co.edtslib.edtsds.R.dimen.dimen_8dp)
                )
            )

            coachMarkView.add(coachData10)

            val rect20 = Rect()
            binding.tv20.getGlobalVisibleRect(rect20)

            val coachData20 = CoachData(
                rect = rect20,
                imageResId = R.drawable.ic_coach_mark_1,
                title = "Title Coachmark 20",
                description = "Deskripsi Coachmark 20 Deskripsi Coachmark 20 Deskripsi Coachmark 20 Deskripsi Coachmark 10",
                sort = 1,
                alignInfo = CoachAlign.Top,
                positiveText = "Selanjutnya",
                trianglePosition = null,
                shape = CoachShape.createRectangle(
                    rad = resources.getDimensionPixelSize(id.co.edtslib.edtsds.R.dimen.dimen_8dp)
                )
            )

            coachMarkView.add(coachData20)

            val rect30 = Rect()
            binding.tv30.getGlobalVisibleRect(rect30)

            val coachData30 = CoachData(
                rect = rect30,
                imageResId = R.drawable.ic_coach_mark_1,
                title = "Title Coachmark 30",
                description = "Deskripsi Coachmark 30 Deskripsi Coachmark 30 Deskripsi Coachmark 30 Deskripsi Coachmark 30",
                sort = 2,
                alignInfo = CoachAlign.Top,
                positiveText = "Selesai",
                trianglePosition = null,
                shape = CoachShape.createRectangle(
                    rad = resources.getDimensionPixelSize(id.co.edtslib.edtsds.R.dimen.dimen_8dp)
                )
            )

            coachMarkView.add(coachData30)

            coachMarkView.show(this)
        }, 1000)
    }
}
