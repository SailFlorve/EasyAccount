package com.hdu.easyaccount;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 添加收入记录的Fragment
 */
public class IncomeRecordFragment extends BaseRecordFragment {

    public IncomeRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void initSettings() {
        moneyText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        isExpense = false;
        typeArray = getResources()
                .getStringArray(R.array.default_income_type);
        int choice = Integer.parseInt((String) prefs.get("default_income_type", "0"));
        classifyText.setText(typeArray[choice]);
        if (isEditMode) {
            setItemData(moneySaved, classifySaved, timeSaved, remarkSaved);
        }
    }
}
