package com.instriker.wcre.presentation

import androidx.databinding.Observable
import com.instriker.wcre.framework.Bindings
import com.instriker.wcre.framework.BindingsFactory
import com.instriker.wcre.framework.IGetterSetter
import com.instriker.wcre.generators.Options.RangeOptions

class InputRangeViewModel private constructor(
        _choices: Array<String>,
        private val _minValue: Int,
        _maxValue: Int,
        private val _quickRangeSetting: IQuickRangeSetting,
        private val _minValueAccessor: IGetterSetter<Int>,
        private val _maxValueAccessor: IGetterSetter<Int>) {

    val quickSettings = BindingsFactory.bindInteger()
    val minValue = BindingsFactory.bindInteger(_minValueAccessor.get())
    val maxValue = BindingsFactory.bindInteger(_maxValueAccessor.get())
    val inclusiveTopLimit = BindingsFactory.bindInteger(_maxValue)
    val choices = BindingsFactory.bindCollection<String>()
    val isCustom = BindingsFactory.bindBoolean()
    val isSingleValue = BindingsFactory.bindBoolean(minValue.get() == this.maxValue.get())

    private var _hasChanges: Boolean = false

    constructor(choices: Array<String>,
                minValue: Int,
                maxValue: Int,
                quickRangeSetting: IQuickRangeSetting,
                rangeOptions: RangeOptions)
            : this(choices, minValue, maxValue, quickRangeSetting, object : IGetterSetter<Int> {
        override fun get(): Int {
            return rangeOptions.minimumItems
        }

        override fun set(value: Int) {
            rangeOptions.minimumItems = value
        }
    }, object : IGetterSetter<Int> {
        override fun get(): Int {
            return rangeOptions.maximumItems
        }

        override fun set(value: Int) {
            rangeOptions.maximumItems = value
        }
    })

    fun hasChanges(): Boolean {
        return this._hasChanges
    }

    fun setNotChanged() {
        this._hasChanges = false
    }

    init {
        Bindings.setCollection(this.choices, _choices)

        this.init()
    }

    private fun init() {
        this.quickSettings.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val index = quickSettings.get()
                
                if (_quickRangeSetting.isCustomChoice(index)) {
                    val actualMax = maxValue.get()
                    val topLimit = inclusiveTopLimit.get()
                    if (actualMax > topLimit) {
                        maxValue.set(topLimit)
                    }
                    this@InputRangeViewModel.isCustom.set(true)
                } else {
                    val range = _quickRangeSetting.getStandardRange(index)

                    this@InputRangeViewModel.minValue.set(range.minValue)
                    this@InputRangeViewModel.maxValue.set(range.maxValue)
                    this@InputRangeViewModel.isCustom.set(false)
                }
            }
        })

        this.minValue.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = minValue.get()
                if (newValue < _minValue) {
                    minValue.set(_minValue)
                    return
                }
                val currentValue = _minValueAccessor.get()
                if (newValue !== currentValue) {
                    _minValueAccessor.set(newValue)
                    _hasChanges = true

                    if (maxValue.get() < newValue) {
                        maxValue.set(newValue)
                    }

                    isSingleValue.set(newValue == maxValue.get())
                }
            }
        })

        this.maxValue.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val newValue = maxValue.get()
                if (newValue < _minValue) {
                    maxValue.set(_minValue)
                    return
                }

                val currentValue = _maxValueAccessor.get()
                if (newValue !== currentValue) {
                    _maxValueAccessor.set(newValue)
                    _hasChanges = true

                    if (minValue.get() > newValue) {
                        minValue.set(newValue)
                    }

                    isSingleValue.set(newValue == minValue.get())
                }
            }
        })
    }

    fun increaseMin() {
        if (this.minValue.get() < this.inclusiveTopLimit.get()) {
            this.minValue.set(this.minValue.get() + 1)
        }
    }

    fun decreaseMin() {
        if (this.minValue.get() > this._minValue) {
            this.minValue.set(this.minValue.get() - 1)
        }
    }

    fun increaseMax() {
        if (this.maxValue.get() < this.inclusiveTopLimit.get()) {
            this.maxValue.set(this.maxValue.get() + 1)
        }
    }

    fun decreaseMax() {
        if (this.maxValue.get() > this._minValue) {
            this.maxValue.set(this.maxValue.get() - 1)
        }
    }

    fun binded(): Any {
        this@InputRangeViewModel.quickSettings.set(_quickRangeSetting.getChoiceIndex(minValue.get(), maxValue.get()))
        return Unit
    }
}
