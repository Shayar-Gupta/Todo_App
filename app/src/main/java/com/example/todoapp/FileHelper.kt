package com.example.todoapp

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

const val FILE_NAME = "todolist.txt"

fun writeData(items: SnapshotStateList<String>, context: Context) {

    val fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)    //fos is a name of an object i.e; file output stream
    var oas = ObjectOutputStream(fos)
    val itemList = ArrayList<String>()
    itemList.addAll(items)
    oas.writeObject(itemList)
    oas.close()
}

fun readData(context: Context): SnapshotStateList<String> {

    var itemList: ArrayList<String>

    try {
        val fis = context.openFileInput(FILE_NAME)   //file input stream
        val ois = ObjectInputStream(fis)
        itemList = ois.readObject() as ArrayList<String>
    } catch (e: FileNotFoundException) {
        itemList = ArrayList()
    }

    val items = SnapshotStateList<String>()
    items.addAll(itemList)
    return items
}

/*
Note:
1). oas  class is used to write data in a file.
2). ois class is used to read data from a file.
 */
