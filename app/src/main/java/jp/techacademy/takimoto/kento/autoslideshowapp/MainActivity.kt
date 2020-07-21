package jp.techacademy.takimoto.kento.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    // 全画像のURIを保存
    var mylist = mutableListOf<Uri>()

    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()

    var index = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


        go_button.setOnClickListener {
            if (index == mylist.size-1){
                index=0
                imageView.setImageURI(mylist[index])
            }else if(index != mylist.size-1){
                index += 1
                imageView.setImageURI(mylist[index])
            }
        }

        back_button.setOnClickListener {
            if (index == 0){
                Log.d("loglog", "URI : " + mylist[index])
                Log.d("loglog", "URI : " + index)
                index=mylist.size-1
                imageView.setImageURI(mylist[index])
                Log.d("loglog", "URI : " + mylist[index])
                Log.d("loglog", "URI : " + index)
            }else if(index !=0){
                index -= 1
                imageView.setImageURI(mylist[index])
            }
        }

        start_button.setOnClickListener {

            go_button.setEnabled(false);
            back_button.setEnabled(false);
            start_button.text="停止"

            if (mTimer == null){
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        // mTimerSec += 2
                        mHandler.post {

                            if (index == mylist.size-1){
                                index=0
                                imageView.setImageURI(mylist[index])
                            }else if(index != mylist.size-1){
                                index += 1
                                imageView.setImageURI(mylist[index])
                            }

                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定
            }else if(mTimer != null){
                if (mTimer != null){
                    mTimer!!.cancel()
                    mTimer = null
                    go_button.setEnabled(true);
                    back_button.setEnabled(true);
                    start_button.text="再生"
                }
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }else{
                    go_button.setEnabled(false);
                    back_button.setEnabled(false);
                    start_button.setEnabled(false);
                    errtext.text="設定から権限を許可して再度アプリを起動してください"
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )



        if (cursor!!.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                mylist.add(imageUri)

//                Log.d("loglog", "URI : " + imageUri.toString())
                Log.d("loglog", "URI : " + mylist.size)

                imageView.setImageURI(mylist[index])

            } while (cursor.moveToNext())
        }
        cursor.close()
        Log.d("loglog", "URI : " + mylist.size)


    }
}
