/* @file LongLatAltDialog.java
 *
 * @author marco corvi
 * @date nov 2012
 *
 * @brief TopoDroid dialog to confirm/enter long-lat data
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.topodroid.DistoX;

// import java.net.URL;
// import java.net.URLConnection;
// import java.net.HttpURLConnection;
// import java.net.URLEncoder;
// import java.net.MalformedURLException;

// import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.io.OutputStream;
// import java.io.BufferedReader;
// import java.io.BufferedInputStream;

import android.net.Uri;

import android.app.Dialog;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.KeyEvent;

import android.inputmethodservice.KeyboardView;
import android.widget.Toast;

import android.util.Log;

public class LongLatAltDialog extends MyDialog
                              implements View.OnClickListener
{
  private LocationDialog mParent;

  private MyKeyboard mKeyboard = null;
  private EditText mEditLong;
  private EditText mEditLat;
  private EditText mEditAlt; // altitude ellipsoid
  private EditText mEditAsl; // altitude geoid

  private Button   mBtnNS;
  private Button   mBtnEW;
  private Button   mBtnOK;
  private Button   mBtnView;

  public LongLatAltDialog( Context context, LocationDialog parent )
  {
    super( context, R.string.LongLatAltDialog );
    mParent  = parent;
  }

// -------------------------------------------------------------------
  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);

    initLayout( R.layout.longlatalt_dialog, R.string.title_coord );

    mEditLong  = (EditText) findViewById(R.id.edit_long );
    mEditLat   = (EditText) findViewById(R.id.edit_lat );
    mEditAlt   = (EditText) findViewById(R.id.edit_alt );
    mEditAsl   = (EditText) findViewById(R.id.edit_asl );

    mKeyboard = new MyKeyboard( mContext, (KeyboardView)findViewById( R.id.keyboardview ), R.xml.my_keyboard, -1 );
    if ( TDSetting.mKeyboard ) {
      int flag = MyKeyboard.FLAG_POINT_DEGREE;
      MyKeyboard.registerEditText( mKeyboard, mEditLong, flag );
      MyKeyboard.registerEditText( mKeyboard, mEditLat,  flag );
      MyKeyboard.registerEditText( mKeyboard, mEditAlt,  MyKeyboard.FLAG_POINT  );
      MyKeyboard.registerEditText( mKeyboard, mEditAsl,  MyKeyboard.FLAG_POINT  );
    } else {
      mKeyboard.hide();
      mEditLong.setInputType( TopoDroidConst.NUMBER_DECIMAL_SIGNED );
      mEditLat.setInputType( TopoDroidConst.NUMBER_DECIMAL_SIGNED );
      mEditAlt.setInputType( TopoDroidConst.NUMBER_DECIMAL );
      mEditAsl.setInputType( TopoDroidConst.NUMBER_DECIMAL );
    }

    if ( mParent.mHasLocation ) {
      mEditLong.setText( FixedInfo.double2string( mParent.mLongitude ) );
      mEditLat.setText(  FixedInfo.double2string( mParent.mLatitude ) );
      mEditAlt.setText(  Integer.toString( (int)(mParent.mHEllipsoid) )  );
      mEditAsl.setText(  Integer.toString( (int)(mParent.mHGeoid) )  );
    }

    mBtnNS = (Button) findViewById(R.id.button_NS);
    mBtnNS.setOnClickListener( this );
    mBtnEW = (Button) findViewById(R.id.button_EW);
    mBtnEW.setOnClickListener( this );
    mBtnOK = (Button) findViewById(R.id.button_ok);
    mBtnOK.setOnClickListener( this );
    mBtnView = (Button) findViewById(R.id.button_view);
    mBtnView.setOnClickListener( this );

  }

  @Override
  public void onClick(View v) 
  {
    Button b = (Button) v;
    // TDLog.Log( TDLog.LOG_INPUT, "LongLatAltDialog onClick() button " + b.getText().toString() ); 

    boolean north = mBtnNS.getText().toString().equals("N");
    boolean east  = mBtnEW.getText().toString().equals("E");
    String longit, latit;
    double lng, lat, alt, asl;

    if ( b == mBtnNS ) {
      mBtnNS.setText( north ? "S" : "N" );
      return;
    } else if ( b == mBtnEW ) {
      mBtnEW.setText( east ? "W" : "E" );
      return;
    } else if ( b == mBtnView ) {
      longit = mEditLong.getText().toString();
      if ( longit == null || longit.length() == 0 ) {
        mEditLong.setError( mContext.getResources().getString( R.string.error_long_required ) );
        return;
      }
      latit = mEditLat.getText().toString();
      if ( latit == null || latit.length() == 0 ) {
        mEditLat.setError( mContext.getResources().getString( R.string.error_lat_required) );
        return;
      }
      lng = FixedInfo.string2double( longit );
      if ( lng < -1000 ) {
        mEditLong.setError( mContext.getResources().getString( R.string.error_long_required ) );
        return;
      } 
      lat = FixedInfo.string2double( latit );
      if ( lat < -1000 ) {
        mEditLat.setError( mContext.getResources().getString( R.string.error_lat_required) );
        return;
      }
      if ( ! north ) lat = -lat;
      if ( ! east )  lng = -lng;

      Uri uri = Uri.parse( "geo:" + lat + "," + lng );
      mContext.startActivity( new Intent( Intent.ACTION_VIEW, uri ) );

    } else if ( b == mBtnOK ) {
   
      // TODO convert string to dec-degrees
      longit = mEditLong.getText().toString();
      if ( longit == null || longit.length() == 0 ) {
        mEditLong.setError( mContext.getResources().getString( R.string.error_long_required ) );
        return;
      }
      latit = mEditLat.getText().toString();
      if ( latit == null || latit.length() == 0 ) {
        mEditLat.setError( mContext.getResources().getString( R.string.error_lat_required) );
        return;
      }
      String altit = mEditAlt.getText().toString();
      String aslit = mEditAsl.getText().toString();
      if ( ( altit == null || altit.length() == 0 ) && (aslit == null || aslit.length() == 0 ) ) {
        mEditAlt.setError( mContext.getResources().getString( R.string.error_alt_required) );
        return;
      }
      lng = FixedInfo.string2double( longit );
      if ( lng < -1000 ) {
        mEditLong.setError( mContext.getResources().getString( R.string.error_long_required ) );
        return;
      } 
      lat = FixedInfo.string2double( latit );
      if ( lat < -1000 ) {
        mEditLat.setError( mContext.getResources().getString( R.string.error_lat_required) );
        return;
      }
      alt = -1000.0;
      asl = -1000.0;
      if ( ( altit == null || altit.length() == 0 ) ) {
        try {
          asl = Double.parseDouble( aslit.replace(",", ".") );
        } catch ( NumberFormatException e ) {
          mEditAsl.setError( mContext.getResources().getString( R.string.error_invalid_number) );
          return;
        }
        WorldMagneticModel wmm = new WorldMagneticModel( mContext );
        alt = wmm.geoidToEllipsoid( lat, lng, asl );
      } else {
        try {
          alt = Double.parseDouble( altit.replace(",", ".") );
        } catch ( NumberFormatException e ) {
          mEditAlt.setError( mContext.getResources().getString( R.string.error_invalid_number) );
          return;
        }
        if ( ( aslit == null || aslit.length() == 0 ) ) {
          WorldMagneticModel wmm = new WorldMagneticModel( mContext );
          asl = wmm.ellipsoidToGeoid( lat, lng, alt );
        } else {
          try {
            asl = Double.parseDouble( aslit.replace(",", ".") );
          } catch ( NumberFormatException e ) {
            mEditAsl.setError( mContext.getResources().getString( R.string.error_invalid_number) );
            return;
          }
        }
      }

      if ( ! north ) lat = -lat;
      if ( ! east )  lng = -lng;

      mParent.addFixedPoint( lng, lat, alt, asl );
    }
    onBackPressed();
  }

  @Override
  public void onBackPressed()
  {
    if ( TDSetting.mKeyboard ) {
      if ( mKeyboard.isVisible() ) {
        mKeyboard.hide();
        return;
      }
    }
    dismiss();
  }

  // @Override 
  // // public boolean onKeyLongPress( int code, KeyEvent ev )
  // public boolean onKeyDown( int code, KeyEvent ev )
  // {
  //   if ( code == KeyEvent.KEYCODE_BACK ) {
  //     onBackPressed();
  //     return true;
  //   } else if ( code == KeyEvent.KEYCODE_MENU ) {
  //     DistoXManualDialog.show Help Page( mContext, R.string.LongLatAltDialog );
  //     return true;
  //   }
  //   return false;
  // }

}

