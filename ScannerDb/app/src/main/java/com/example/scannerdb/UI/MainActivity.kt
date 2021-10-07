package com.example.scannerdb.UI

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.scannerdb.DataBase.DataBaseHelper
import com.example.scannerdb.Model.CartModel
import com.example.scannerdb.Model.ProductModel
import com.example.scannerdb.R
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DataBaseHelper
    private lateinit var mArrylist: ArrayList<ProductModel>
    private lateinit var mArrylistCart: ArrayList<CartModel>
    private var cartAdapter: CartAdapter? = null
    private var TAG: String = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mArrylist = ArrayList()
        mArrylistCart = ArrayList()
        val rcProduct:RecyclerView = findViewById(R.id.rcProduct)
        rcProduct.layoutManager = GridLayoutManager(this@MainActivity,1)
        cartAdapter = CartAdapter(this@MainActivity, mArrylistCart)
        rcProduct.adapter = cartAdapter

        val scanCustomCode = registerForActivityResult(ScanCustomCode(), ::handleResult)

        databaseHelper = DataBaseHelper(this@MainActivity)

            aadToProductTable(
                "0001",
                "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=0001",
                "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/apple/237/banana_1f34c.png",
                "Banana",
                "$1.00"
            )
            aadToProductTable(
                "0002",
                "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=0002",
                "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/apple/237/red-apple_1f34e.png",
                "Apple",
                "$4.00"
            )
            aadToProductTable(
                "0003",
                "https://zxing.org/w/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl=0003",
                "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/apple/237/sparkles_2728.png",
                "Other Stuff",
                "$10.00"
            )

        val tvOpenScanner = findViewById<TextView>(R.id.tvOpenScanner)
        tvOpenScanner.setOnClickListener {


            scanCustomCode.launch(
                ScannerConfig.build {
                    setBarcodeFormats(listOf(BarcodeFormat.FORMAT_ALL_FORMATS)) // set interested barcode formats
                    setHapticSuccessFeedback(false) // enable (default) or disable haptic feedback when a barcode was detected
                    setShowTorchToggle(true) // show or hide (default) torch/flashlight toggle button
                }
            )
        }

        val tvClearData = findViewById<TextView>(R.id.tvClearData)
        tvClearData.setOnClickListener {
            databaseHelper.clearCartTable()

            val getDataFromSQLiteCart = GetDataFromSQLiteCartTable()
            getDataFromSQLiteCart.execute()

            val tvTotal = findViewById<TextView>(R.id.tvTotal)
            tvTotal.text = "$0"
        }


    }

    fun handleResult(result: QRResult) {

        Log.e(TAG,"result from qrcode" + result.toString())

        val strFinalId = result.toString().replace("QRSuccess(content=Plain(rawValue=","").replace("))","")
        Log.e(TAG,"result from qrcode" + strFinalId)

        addtoCartScanQrCode(strFinalId)

    }

    fun addtoCartScanQrCode(strFinalId:String){

        var isItemAdded = false
        for (i in 0 until mArrylist.size){
            if (strFinalId == mArrylist[i].productId){
                addProductToCart(i,strFinalId)
                isItemAdded = true
            }
        }

        if (!isItemAdded){
            Toast.makeText(this, "In valid Qr Code ", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Added To Cart ", Toast.LENGTH_SHORT).show()
        }

    }

    private fun addProductToCart(position: Int, strFinalId: String) {


        if (!databaseHelper.checkProductToCart(mArrylist[position].productId)) {

            val product = CartModel(
                productId = mArrylist[position].productId,
                qrUrl = mArrylist[position].qrUrl,
                thumbnail = mArrylist[position].thumbnail,
                name = mArrylist[position].name,
                price = mArrylist[position].price,
                quantity = "1"

            )
            databaseHelper.addProductToCart(product)



        }else{

            for (i in 0 until mArrylistCart.size){

                if (strFinalId == mArrylistCart[i].productId){

                    val product = CartModel(
                        productId = mArrylistCart[i].productId,
                        qrUrl = mArrylistCart[i].qrUrl,
                        thumbnail = mArrylistCart[i].thumbnail,
                        name = mArrylistCart[i].name,
                        price = mArrylistCart[i].price,
                        quantity = (mArrylistCart[i].quantity.toInt() + 1).toString()

                    )
                    databaseHelper.updateProductToCart(product)
                }
            }


        }
    }

    fun aadToProductTable(productId: String, qRCode: String, image: String, productName: String, price: String) {

        if (!databaseHelper.checkProduct(productName)) {

            val product = ProductModel(
                productId = productId,
                qrUrl = qRCode,
                thumbnail = image,
                name = productName,
                price = price

            )
            databaseHelper.addProduct(product)

        }
    }

    override fun onResume() {
        super.onResume()

        val getDataFromSQLite = GetDataFromSQLiteProductTable()
        getDataFromSQLite.execute()

        val getDataFromSQLiteCart = GetDataFromSQLiteCartTable()
        getDataFromSQLiteCart.execute()


    }

    inner class GetDataFromSQLiteProductTable : AsyncTask<Void, Void, List<ProductModel>>() {

        override fun doInBackground(vararg p0: Void?): List<ProductModel> {
            return databaseHelper.getAllProduct()         // Calling all the values from the data base
        }

        override fun onPostExecute(result: List<ProductModel>?) {    //When Post method Execution Happen
            super.onPostExecute(result)
            mArrylist.clear()     //Clear List If Any Data In it
            mArrylist.addAll(result!!) //Add Data In List Come From DataBaseHelper

            if(mArrylist.size>0){

                Log.e(TAG, "Data In Data Base$mArrylist")

            }else{
                Log.e(TAG, "No Data In Data Base")
            }

        }
    }

    inner class GetDataFromSQLiteCartTable : AsyncTask<Void, Void, List<CartModel>>() {

        override fun doInBackground(vararg p0: Void?): List<CartModel>? {
            return databaseHelper.getAllCartProduct()         // Calling all the values from the data base
        }

        override fun onPostExecute(result: List<CartModel>?) {    //When Post method Execution Happen
            super.onPostExecute(result)
            mArrylistCart.clear()     //Clear List If Any Data In it
            mArrylistCart.addAll(result!!) //Add Data In List Come From DataBaseHelper
            val tvNoData = findViewById<TextView>(R.id.tvNoData)
            if(mArrylistCart.size>0){

                Log.e(TAG, "Data In Data Base cart${mArrylistCart.size}")
                Log.e(TAG, "Data In Data Base cart$mArrylistCart")

                    cartAdapter!!.notifyDataSetChanged()
                tvNoData.visibility = View.GONE

                totalValueAdded()

            }else{
                Log.e(TAG, "No Data In Data Base cart")
                cartAdapter!!.notifyDataSetChanged()
                tvNoData.visibility = View.VISIBLE
            }

        }
    }

    private fun totalValueAdded() {

        var total = 0F
        for (i in 0 until mArrylistCart.size){
            val price = mArrylistCart[i].price.replace("$","")
            total += (price.toFloat() * mArrylistCart[i].quantity.toInt())
        }

        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        tvTotal.text = "$" + total.toString()

    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.clearTable()
    }

    class CartAdapter(private var context:Activity, private var mArrayList: ArrayList<CartModel>):RecyclerView.Adapter<CartAdapter.ViewHolder>(){

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var ivProduct: ImageView = view.findViewById(R.id.ivProduct)
            var tvProductName: TextView = view.findViewById(R.id.tvProductName)
            var tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
            var tvSubTotal: TextView = view.findViewById(R.id.tvSubTotal)
            var tvPrice: TextView = view.findViewById(R.id.tvPrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
            )
        }

        override fun onBindViewHolder(holder: CartAdapter.ViewHolder, position: Int) {
            Log.e("CartAdapter","InsideonBindViewHolder")
            Log.e("CartAdapter","ArraySize" + mArrayList.size)

            holder.tvProductName.text = mArrayList[position].name

            Glide.with(context).load(mArrayList[position].thumbnail).into(holder.ivProduct)
            holder.tvQuantity.text = mArrayList[position].quantity + " x " + mArrayList[position].price
            val price = mArrayList[position].price.replace("$","")
            holder.tvSubTotal.text = "$" + (mArrayList[position].quantity.toInt() * price.toFloat()).toString()

        }

        override fun getItemCount(): Int {
            return mArrayList.size
        }

    }
}