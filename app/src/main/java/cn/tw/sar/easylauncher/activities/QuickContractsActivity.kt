package cn.tw.sar.easylauncher.activities

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cn.tw.sar.easylauncher.R
import cn.tw.sar.easylauncher.beam.Contract
import cn.tw.sar.easylauncher.beam.DesktopIcon
import cn.tw.sar.easylauncher.database.QuickContractsDatabase
import cn.tw.sar.easylauncher.entity.QuickContracts
import cn.tw.sar.easylauncher.ui.theme.EasyLauncherTheme
import cn.tw.sar.easylauncher.utils.ContractUtils
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import cn.tw.sar.easylauncher.utils.getUnDarkModeTextColor
import cn.tw.sar.easylauncher.weight.MyDialog
import kotlin.concurrent.thread

class QuickContractsActivity : ComponentActivity() {
    var allList = mutableStateListOf<QuickContracts>()
    var isOk = mutableStateOf(false )
    var isFull = mutableStateOf(false)

    var editCode = mutableStateOf<QuickContracts?>(null)
    var isShowYz = mutableStateOf(false)
    var showDialog = mutableStateOf(false)
    var deleteID = mutableStateOf(0L)

    @Composable
    fun Dialogs(){
        // 这里都是弹窗 太多了
        Column {
            var fontColor = getDarkModeTextColor(this@QuickContractsActivity)
            var widthDp = Resources.getSystem().displayMetrics.heightPixels / Resources.getSystem().displayMetrics.density

            MyDialog(
                onDismissRequest = {
                    if (isShowYz.value) {
                        isShowYz.value = false
                        editCode.value = null
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false
                ),
                showDialog = isShowYz.value,
                fontColor = fontColor,
                subBackgroundColor = getDarkModeBackgroundColor(
                    this@QuickContractsActivity, 1
                ),
                content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)

                        ) {
                            Text(
                                text = if (editCode.value==null) "添加" else "修改", color = fontColor,
                                fontSize = 20.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (editCode.value==null) "添加" else {
                                    "修改" + editCode.value!!.name+""
                                }, color = fontColor,
                                fontSize = 15.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val code  = remember { mutableStateOf( if (editCode.value==null) "" else {
                                editCode.value!!.name
                            }) }

                            TextField(
                                value =  code.value,
                                onValueChange = {
                                    code.value = it
                                    editCode.value!!.name = it
                                },
                                placeholder = {
                                    Text("请输入姓名")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val phone  = remember { mutableStateOf( if (editCode.value==null) "" else {
                                editCode.value!!.phone
                            }) }

                            TextField(
                                value =  phone.value,
                                onValueChange = {
                                    phone.value = it
                                    editCode.value!!.phone = it
                                },
                                placeholder = {
                                    Text("请输入电话号码")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(10.dp))


                            Button(
                                onClick = {
                                    thread {
                                        val database: QuickContractsDatabase =
                                            QuickContractsDatabase.getDatabase(this@QuickContractsActivity)
                                        val codeDao = database.quickContractsDao()
                                        // 编辑后会移除同步状态
                                        if (editCode.value != null) {
                                            codeDao.update(editCode.value!!)
                                        }else{
                                            codeDao.insert(QuickContracts(
                                                id = 0,
                                                name = code.value,
                                                phone = phone.value,
                                            ))
                                        }

                                        runOnUiThread {
                                            onResume()
                                            editCode.value = null
                                            Toast.makeText(this@QuickContractsActivity, "成功", Toast.LENGTH_SHORT).show()
                                            isShowYz.value =  false

                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(text = "保存")

                            }
                        }

                },
                modifier = Modifier.fillMaxSize()
            )
            MyDialog(
                onDismissRequest = {
                    if (showDialog.value) {
                        showDialog.value = false
                        deleteID.value = 0
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false
                ),
                showDialog = showDialog.value,
                fontColor = fontColor,
                subBackgroundColor = getDarkModeBackgroundColor(
                    this@QuickContractsActivity, 1
                ),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)

                    ) {
                        Text(
                            text = "确定删除吗？", color = fontColor,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "删除后不可恢复", color = fontColor,
                            fontSize = 15.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = {
                                showDialog.value = false
                            }) {
                                Text(text = "取消", color = fontColor)
                            }
                            Button(onClick = {
                                showDialog.value = false
                                //onDelete(format.id)
                                if (deleteID.value != 0L) {
                                    thread {
                                        val database: QuickContractsDatabase =
                                            QuickContractsDatabase.getDatabase(this@QuickContractsActivity)
                                        val codeDao = database.quickContractsDao()
                                        var codeItem = codeDao.deleteById(deleteID.value)

                                        runOnUiThread {
                                            onResume()
                                            deleteID.value = 0L
                                            Toast.makeText(this@QuickContractsActivity, "删除成功", Toast.LENGTH_SHORT).show()
                                        }

                                    }
                                } else {
                                    Toast.makeText(this@QuickContractsActivity, "删除失败", Toast.LENGTH_SHORT).show()
                                }

                            }) {
                                Text(text = "确定", color = fontColor)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    @Composable
    fun Greeting() {
        // LazyColumn 加载所有联系人
        Dialogs()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = getDarkModeBackgroundColor(
                        this@QuickContractsActivity,
                        0
                    )
                )

        ) {
            for (contract in allList) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(
                            color = getDarkModeBackgroundColor(
                                this@QuickContractsActivity,
                                0
                            )
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween


                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .background(
                                color = getDarkModeBackgroundColor(
                                    this@QuickContractsActivity,
                                    0
                                )
                            )
                    ) {
                        Text(
                            text = contract.name!!,
                            fontSize = 20.sp,
                            color = getDarkModeTextColor(this@QuickContractsActivity)
                        )
                        Text(
                            text = contract.phone,
                            fontSize = 15.sp,
                            color = getDarkModeTextColor(this@QuickContractsActivity)
                        )
                    }

                    Row(
                    ) {
                        TextButton(onClick = {
                            deleteID.value = contract.id
                            showDialog.value = true

                        }) {
                            Text(
                                text = "删除",
                                fontSize = 15.sp,
                                color = getDarkModeTextColor(this@QuickContractsActivity)
                            )
                        }

                        TextButton(onClick = {
                            isShowYz.value = true
                            editCode.value = contract
                        }) {
                            Text(
                                text = "编辑",
                                fontSize = 15.sp,
                                color = getDarkModeTextColor(this@QuickContractsActivity)
                            )
                        }
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        allList.clear()
        val database: QuickContractsDatabase =
            QuickContractsDatabase.getDatabase(this@QuickContractsActivity)
        val quickContractsDao = database.quickContractsDao()
        thread {
            val all = quickContractsDao.getAll()
            allList.addAll(all)
            isOk.value = true
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("QuickContractsActivity", "allList: $allList")
        enableEdgeToEdge()
        setContent {

            EasyLauncherTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    floatingActionButton = {
                        IconButton(
                            onClick = {
                                isShowYz.value = true
                            },
                            modifier = Modifier
                                .padding(
                                    end = 20.dp,
                                    start = 5.dp,
                                    top = 10.dp,
                                    bottom = 5.dp
                                )
                                .size(60.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.shapes.extraLarge
                                )

                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "add",
                                tint = Color.White,
                            )
                        }
                    }
                ){ innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize().padding(innerPadding)
                            .background(
                                color = getDarkModeBackgroundColor(
                                    this@QuickContractsActivity,
                                    0
                                )
                            )
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(10.dp)
                    ) {
                        Text(text = "快捷联系人", fontSize = 30.sp,
                            modifier = Modifier.padding(10.dp,30.dp,10.dp,30.dp)
                            , color = getDarkModeTextColor(this@QuickContractsActivity)
                        )
                        Greeting()
                    }
                }

            }
        }
    }
}

