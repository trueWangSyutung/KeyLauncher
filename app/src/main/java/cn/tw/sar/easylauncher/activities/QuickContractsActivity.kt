package cn.tw.sar.easylauncher.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.easylauncher.beam.Contract
import cn.tw.sar.easylauncher.beam.DesktopIcon
import cn.tw.sar.easylauncher.ui.theme.EasyLauncherTheme
import cn.tw.sar.easylauncher.utils.ContractUtils
import cn.tw.sar.easylauncher.utils.getDarkModeBackgroundColor
import cn.tw.sar.easylauncher.utils.getDarkModeTextColor
import cn.tw.sar.easylauncher.utils.getUnDarkModeTextColor

class QuickContractsActivity : ComponentActivity() {
    var allList: Set<Contract> = mutableSetOf()
    var isOk = mutableStateOf(false )
    var isFull = mutableStateOf(false)
    @Composable
    fun Greeting() {
        var sp = getSharedPreferences("quick", MODE_PRIVATE)
        isFull.value = ContractUtils.checkQuickContractFull(this@QuickContractsActivity)
        // LazyColumn 加载所有联系人
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
                var isQuicked = remember {
                    mutableStateOf(
                        sp.getBoolean(
                            contract.name+"_"+contract.phone,
                            false
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(
                            color = getDarkModeBackgroundColor(
                                this@QuickContractsActivity,
                                0
                            )
                        )


                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(
                                fraction = 0.65f
                            )
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
                    AnimatedVisibility(visible = isQuicked.value) {
                        Button(
                            onClick = {
                                isQuicked.value = false
                                sp.edit().putBoolean(
                                    contract.name+"_"+contract.phone,
                                    false
                                ).apply()
                                isFull.value = ContractUtils.checkQuickContractFull(this@QuickContractsActivity)
                            },
                            modifier = Modifier
                                .padding(10.dp).fillMaxWidth()

                        ) {
                            Text(
                                text = "删除",
                                fontSize = 15.sp,
                                color = getUnDarkModeTextColor(
                                    this@QuickContractsActivity,
                                )
                            )
                        }

                    }
                    AnimatedVisibility(visible = (!isQuicked.value )) {
                        Button(
                            onClick = {
                                if (isFull.value) {
                                    Toast.makeText(
                                        this@QuickContractsActivity,
                                        "快捷联系人已满，最多可以添加8个",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }else{
                                    isQuicked.value = true
                                    sp.edit().putBoolean(
                                        contract.name + "_" + contract.phone,
                                        true
                                    ).apply()
                                }
                                isFull.value = ContractUtils.checkQuickContractFull(this@QuickContractsActivity)
                            },
                            modifier = Modifier
                                .padding(10.dp).fillMaxWidth()

                        ) {
                            Text(
                                text = "添加",
                                fontSize = 15.sp,
                                color = getUnDarkModeTextColor(
                                    this@QuickContractsActivity,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allList = ContractUtils.readContractByNumberStartWith(this@QuickContractsActivity)
        Log.d("QuickContractsActivity", "allList: $allList")
        setContent {
            EasyLauncherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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

