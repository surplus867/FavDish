package com.example.favdish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favdish.R
import com.example.favdish.databinding.ActivityAddUpdateDishBinding
import com.example.favdish.databinding.DialogCustomImageSelectionBinding
import com.example.favdish.databinding.DialogCustomListBinding
import com.example.favdish.utils.Constants
import com.example.favdish.view.adapters.CustomListItemAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUpdateDishBinding
    private var imagePath : String = ""

    private lateinit var customListDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupActionBar()

        binding.ivAddDishImage.setOnClickListener(this)

        binding.etType.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.etCookingTimesInMinutes.setOnClickListener(this)

        binding.btnAddDish.setOnClickListener(this)

    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                    return
                }
                R.id.et_type -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_type),
                        Constants.dishTypes(),
                        Constants.DISH_TYPE
                    )
                    return
                }

                R.id.et_category -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_category),
                        Constants.dishCategories(),
                        Constants.DISH_CATEGORY
                    )
                    return
                }

                R.id.et_cooking_times_in_minutes -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_cooking_time),
                        Constants.dishCookingTime(),
                        Constants.DISH_COOKING_TIME
                    )
                    return

                }

                R.id.btn_add_dish -> {
                    val title = binding.etTitle.text.toString().trim { it <= ' ' }
                    val type = binding.etType.text.toString().trim { it <= ' ' }
                    val category = binding.etCategory.text.toString().trim { it <= ' ' }
                    val ingredients = binding.etIngredients.text.toString().trim { it <= ' ' }
                    val cookingTimeInMinutes = binding.etCookingTimesInMinutes.text.toString().trim { it <= ' ' }
                    val cookingDirection = binding.etDirectionToCook.text.toString().trim { it <= ' ' }

                    when {
                        TextUtils.isEmpty(imagePath) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_image),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(title) -> {
                          Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_title),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_type),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_category),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_ingredients),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(cookingTimeInMinutes) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_cooking_time),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_cooking_instructions),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                this@AddUpdateDishActivity,
                                "All the entries are valid.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            }

        }
    }


    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            // TODO fix but this is working
            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startForResultToLoadImage.launch(intent)
            }*/


            Dexter.withContext(this)
                .withPermissions(
                    //Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .withListener(object: MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                startForResultToLoadImage.launch(intent)
                            } else {
                                ActivityCompat.requestPermissions(this@AddUpdateDishActivity, arrayOf(Manifest.permission.CAMERA), 0)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        // TODO show alert dialog
                        showRationDialogForPermissions()
                    }

                }
                ).onSameThread()
                .check()

            dialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ).withListener(object: PermissionListener {
                    override fun onPermissionGranted(report: PermissionGrantedResponse?) {
                        report?.let {
                            val galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startForResultToLoadImage.launch(galleryIntent)
                        }

                        /*    Toast.makeText(
                                this@AddUpdateDishActivity,
                                "You have the Gallery permission now to select image.",
                                Toast.LENGTH_SHORT
                            ).show()*/
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@AddUpdateDishActivity,
                            "you have denied the storage permission to select image.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationDialogForPermissions()
                    }


                }).onSameThread()
                .check()
            // END
            dialog.dismiss()
        }

        dialog.show()

    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constants.DISH_TYPE -> {
                customListDialog.dismiss()
                binding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                customListDialog.dismiss()
                binding.etCategory.setText(item)
            }
            else -> {
                customListDialog.dismiss()
                binding.etCookingTimesInMinutes.setText(item)
            }
        }
    }

    private val startForResultToLoadImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val selectedImage: Uri? = result.data?.data
                if(selectedImage != null){
                    //val options = RequestOptions().centerCrop()

                    Glide.with(this)
                        .load(selectedImage)
                        .centerCrop()
                        .into(binding.ivDishImage)

                }else{
                    // From Camera code goes here.
                    // Get the bitmap directly from camera
                    result.data?.extras?.let {
                        val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
                        //val options = RequestOptions().centerInside()

                        Glide.with(this)
                            .load(bitmap)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object : RequestListener<Drawable>{
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                    Log.e("TAG", "Error Loading image", e)
                                    return false
                                }

                                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    resource?.let{
                                        val bitmap: Bitmap = resource.toBitmap()
                                        imagePath = saveImageToInternalStorage(bitmap)
                                        Log.i("ImagePath", imagePath)
                                    }
                                    return false
                                }


                            })
                            .into(binding.ivDishImage)

                        imagePath = saveImageToInternalStorage(bitmap)

                        //Log.i("ImagePath", imagePath)

                        binding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))
                    }
                }
            } catch (error: Exception) {
                Log.d("Log==>>", "Error : ${error.localizedMessage}")
            }

        }
    }

    private fun showRationDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like you have turn off permissions" +
                " required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS")
            {_,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg" )

        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemsListDialog(title: String, itemsList: List<String>, selection: String){
        customListDialog = Dialog(this)
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        customListDialog.setContentView(binding.root)
        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(this)

        val adapter = CustomListItemAdapter(this, itemsList, selection)
        binding.rvList.adapter = adapter
        customListDialog.show()


    }

    companion object{
        private const val IMAGE_DIRECTORY = "MinyuKimImages"
    }
}