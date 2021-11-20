package com.instriker.wcre.framework

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ViewFlipper

class HorizontalSwipeGestureListener(internal var _flipper: ViewFlipper) : SimpleOnGestureListener() {

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        println(" in onFling() :: ")
        if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH)
            return false
        if (e1.x - e2.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            _flipper.inAnimation = inFromRightAnimation()
            _flipper.outAnimation = outToLeftAnimation()
            _flipper.showNext()
        } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            _flipper.inAnimation = inFromLeftAnimation()
            _flipper.outAnimation = outToRightAnimation()
            _flipper.showPrevious()
        }
        return super.onFling(e1, e2, velocityX, velocityY)
    }

    private fun inFromRightAnimation(): Animation {
        val inFromRight = TranslateAnimation(Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 0.0f)
        inFromRight.duration = ANIMATION_DURATION.toLong()
        inFromRight.interpolator = AccelerateInterpolator()
        return inFromRight
    }

    private fun outToLeftAnimation(): Animation {
        val outtoLeft = TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 0.0f)
        outtoLeft.duration = ANIMATION_DURATION.toLong()
        outtoLeft.interpolator = AccelerateInterpolator()
        return outtoLeft
    }

    private fun inFromLeftAnimation(): Animation {
        val inFromLeft = TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 0.0f)
        inFromLeft.duration = ANIMATION_DURATION.toLong()
        inFromLeft.interpolator = AccelerateInterpolator()
        return inFromLeft
    }

    private fun outToRightAnimation(): Animation {
        val outtoRight = TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 0.0f)
        outtoRight.duration = ANIMATION_DURATION.toLong()
        outtoRight.interpolator = AccelerateInterpolator()
        return outtoRight
    }

    companion object {

        private val SWIPE_MIN_DISTANCE = 120
        private val SWIPE_MAX_OFF_PATH = 250
        private val SWIPE_THRESHOLD_VELOCITY = 200
        private val ANIMATION_DURATION = 250
    }
}