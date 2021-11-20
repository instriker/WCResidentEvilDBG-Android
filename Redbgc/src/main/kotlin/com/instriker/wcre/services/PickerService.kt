package com.instriker.wcre.services

import com.instriker.wcre.R
import com.instriker.wcre.presentation.PickerViewModel

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Binder
import android.view.View

class PickerService(private val _context: Context) {

    fun <TItem> createAndBindDialog(titleId: Int, layoutId: Int, contentViewModel: PickerViewModel<TItem>): AlertDialog {
        val builder = AlertDialog.Builder(_context)
        builder
                .setTitle(titleId)
                .setNegativeButton(R.string.cancel) { dialog, _ -> contentViewModel.onCancelled() }

        // TODO: Convert to DataBinding library
        throw RuntimeException("TODO PickerService")
        //        InflateResult result = Binder.inflateView(_context, layoutId, null, false);
        //        for (View v : result.processedViews) {
        //            AttributeBinder.getInstance().bindView(_context, v, contentViewModel);
        //        }
        //        builder.setView(result.rootView);

        //        return builder.create();
    }

    fun <TItem> pickItem(titleId: Int, layoutId: Int, values: Collection<TItem>, listener: PickerViewModel.IPickedResultListener<TItem>) {
        val viewModel = PickerViewModel(values)

        val dialog = createAndBindDialog(titleId, layoutId, viewModel)
        viewModel.setPickedResultListener(object : PickerViewModel.IPickedResultListener<TItem> {
            override fun onItemChoosen(choosen: TItem) {
                dialog.dismiss()
                listener.onItemChoosen(choosen)
            }

            override fun onPickerCancelled() {
                listener.onPickerCancelled()
            }
        })
        dialog.show()
    }
}