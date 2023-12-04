package cs2063.groceryshopper.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import cs2063.groceryshopper.R
import cs2063.groceryshopper.TripActivity
import cs2063.groceryshopper.model.Item
import java.util.Locale
import java.util.concurrent.Executors

var activityGlobal : Activity? = null
var itemDetailsGlobal: ArrayList<ArrayList<String>>? = null

class ListOfEditorsGenerator {
    fun generateList(activity : Activity, itemDetails: ArrayList<ArrayList<String>>) {
        activityGlobal = activity
        itemDetailsGlobal = itemDetails
        Executors.newSingleThreadExecutor()
            .execute {
                val mainHandler = Handler(Looper.getMainLooper())
                val listOfItems = buildData(itemDetails)
                mainHandler.post {
                    updateDisplay(activity, listOfItems!!)
                }

            }
    }

    private fun updateDisplay(activity: Activity, listOfItems: ArrayList<Map<String, String>>){
        val listView : ListView = activity.findViewById(R.id.editorsList)

        val adapter = MyEditorsAdapter(
            activity,
            listOfItems,
            R.layout.item_add,
            arrayOf("name", "cost"),
            intArrayOf(R.id.name_edit_text, R.id.price_edit_text)
        )
        listView.adapter = adapter
    }

    private fun buildData(itemDetails: ArrayList<ArrayList<String>>) : ArrayList<Map<String, String>> {
        val list = ArrayList<Map<String,String>>()
        // ToDo: Read the items from the OCR Text
        //      My suggestion here is to look at each line and any line with a price it
        //      in we assume is an item and we take the item and all the text from that
        //      line and put it in the editor area. Then the user can do the final clean up
        for(details in itemDetails){
            list.add(putData(details[0], details[1]))
        }
        return list
    }

    private fun putData(name : String, cost : String) : HashMap<String, String>{
        val item = HashMap<String, String>()
        item["name"] = name
        item["cost"] = "%,.2f".format(Locale.ENGLISH, cost.toDouble())
        return item
    }
}

class MyEditorsAdapter(
    context: Context,
    data: ArrayList<Map<String, String>>,
    @LayoutRes
    res: Int,
    from: Array<String>,
    @IdRes
    to: IntArray
): SimpleAdapter(context, data, res, from, to) {
    override fun getView(position: Int, viewIn: View?, parent: ViewGroup) : View {
        val view = super.getView(position, viewIn, parent)

        val deleteButton : Button = view.findViewById<Button>(R.id.deleteEditor)
        deleteButton.setOnClickListener {
            Log.i("Hello", "Hello")

            for (i in 0..<parent!!.childCount){
                val item: RelativeLayout = parent?.getChildAt(i) as RelativeLayout
                val itemName = item.findViewById<EditText>(R.id.name_edit_text).text.toString()
                val price = item.findViewById<EditText>(R.id.price_edit_text).text.toString().toDouble()
                itemDetailsGlobal?.get(i)?.set(0, itemName)
                itemDetailsGlobal?.get(i)?.set(1, price.toString())
            }
            itemDetailsGlobal!!.removeAt(position)
            val listOfEditorsGenerator = ListOfEditorsGenerator()
            listOfEditorsGenerator.generateList(activityGlobal!!, itemDetailsGlobal!!)
        }
        return view
    }
}

