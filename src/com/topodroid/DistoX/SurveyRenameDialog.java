/** @file SurveyRenameDialog.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * @brief TopoDroid survey Rename dialog
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

import android.app.Dialog;
// import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.content.Context;

import android.widget.EditText;
import android.widget.Button;
import android.view.View;
// import android.view.View.OnKeyListener;
// import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;


public class SurveyRenameDialog extends MyDialog
                                implements View.OnClickListener
{
  private EditText mEtName;
  private Button   mBtnOK;
  // private Button   mBtnCancel;

  private SurveyActivity mParent;

  SurveyRenameDialog( Context context, SurveyActivity parent )
  {
    super( context, R.string.SurveyRenameDialog );
    mParent = parent;
  }

// -------------------------------------------------------------------
  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.survey_rename_dialog);
    getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
    mBtnOK  = (Button) findViewById(R.id.btn_ok );
    // mBtnCancel = (Button) findViewById(R.id.btn_cancel );

    mBtnOK.setOnClickListener( this );
    // mBtnCancel.setOnClickListener( this );

    mEtName = (EditText) findViewById( R.id.et_name );
    mEtName.setText( mParent.getSurveyName( ) );

    setTitle( R.string.title_survey_rename );
  }

  public void onClick(View v) 
  {
    // When the user clicks, just finish this activity.
    // onPause will be called, and we save our data there.
    Button b = (Button) v;
    if ( b == mBtnOK ) {
      mParent.renameSurvey( mEtName.getText().toString() );
    // } else if ( b == mBtnCancel ) {
    //   // nothing
    }
    dismiss();
  }

}


