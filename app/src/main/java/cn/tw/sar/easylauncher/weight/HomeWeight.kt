package cn.tw.sar.easylauncher.weight

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.tw.sar.easylauncher.MainActivity
import cn.tw.sar.easylauncher.R
import cn.tw.sar.easylauncher.SettingsActivity
import cn.tw.sar.easylauncher.beam.DesktopIcon
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Preview(showBackground = true)
@Composable
fun LineBar(
    maxPage: Int = 5,
    page: Int = 1,
    onLeftEnd: () -> Unit = {
        println("end")
    },
    onRightEnd: () -> Unit = {
        println("end")
    },
    onDotsClick: (Int) -> Unit = {
        println("click")
    }
){
    var targetOffsetX = remember { mutableStateOf(0f) }//Activity的最终偏移目标
    val activityOffset = remember { Animatable(0f) }//Activity的偏移，动画
    var dragging by remember { mutableStateOf(false) }//是否正在被滑动
    // 显示下方的滚动条
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    //跟踪滑动偏移
                    targetOffsetX.value += delta
                },
                onDragStarted = {
                    dragging = true
                },

                onDragStopped = {
                    //滑动停止时，根据偏移目标，判断是否需要切换页面
                    // 判断偏移量
                    if (targetOffsetX.value > 0) {
                        onLeftEnd()

                        targetOffsetX.value = 0f

                    } else if (targetOffsetX.value < 0) {
                        onRightEnd()
                        targetOffsetX.value = 0f
                    } else {
                        // 判断点击的元素


                    }

                },
                startDragImmediately = true


            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..maxPage) {
            if (i == page) {
                Image(
                    painter = painterResource(id = R.drawable.dots),
                    contentDescription = null,
                    modifier = Modifier
                        .width(10.dp)
                        .height(10.dp)
                        .padding(2.dp)
                        .clickable {
                            onDotsClick(i)
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.dots_un),
                    contentDescription = null,
                    modifier = Modifier
                        .width(10.dp)
                        .height(10.dp)
                        .padding(2.dp)
                        .clickable {
                            onDotsClick(i)
                        }
                )
            }
        }


    }
}



@Preview(showBackground = false)
@Composable
fun KeyBoard(
    func: (String) -> Unit = {
        println(it)
    },
    page: Int = 0,
    mode:Boolean = false,
    width: Float = 300f,
    height: Float = 300f,
    backgroundColor: Color = Color.Black,
    buttonColor: Color = Color.DarkGray,
    fontColor: Color = Color.Black,
    context: Context = LocalContext.current
){
    var list = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )
    var subList = listOf(
        listOf("easy", "ABC", "DEF"),
        listOf("GHI", "JKL", "MNO"),
        listOf("PQRS", "TUV", "WXYZ"),
        listOf(",", "+", ".")
    )
    Column(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .background(color = backgroundColor)
            .padding(2.dp)

    ) {
        var newHight = width*0.70f
        var topHight = height - newHight
        Row(
            modifier = Modifier
                .width(width.dp)
                .height(topHight.dp)
                .padding(2.dp)
                .background(color = backgroundColor),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var btnColors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White,
                disabledContentColor = Color.White ,
                disabledContainerColor = buttonColor
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Button(
                        onClick = {
                            func("open")
                        },
                        colors = btnColors,
                        modifier = Modifier
                            .width((width / 3).dp)
                            .padding(5.dp)
                            .background(buttonColor, MaterialTheme.shapes.small),

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {
                           // Icon(imageVector = Icons.Filled.Done, contentDescription = "Phone", tint = Color(0xFF00FF00))
                            Text(text = if (page==0 && !mode){
                                                    context.resources.getString(R.string.confrim)
                                                    }else{
                                context.resources.getString(R.string.open)
                                                         }, color = Color(0xFF00FF00))
                        }

                    }
                    Button(
                        onClick = {
                            func("call")
                        },
                        colors = btnColors,
                        modifier = Modifier
                            .width((width / 3).dp)
                            .padding(5.dp)
                            .background(buttonColor, MaterialTheme.shapes.medium),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {
                            /// Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone", tint = Color(0xFF00FF00))
                            Text(text = context.resources.getString(R.string.call), color = Color(0xFF00FF00))
                        }

                    }
                }
                Box(
                    modifier = Modifier
                        .size((width / 3).dp)
                        .padding(2.dp).background(
                            buttonColor, MaterialTheme.shapes.extraLarge
                        ).padding(2.dp),
                    contentAlignment =  Alignment.Center

                ){
                    IconButton(
                        onClick = {
                            func("up")
                        },
                        Modifier
                            .align(Alignment.TopCenter)
                            .background(buttonColor, MaterialTheme.shapes.extraLarge),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Phone", tint = Color(0xFFFF0000)
                        )
                    }

                    IconButton(
                        onClick = {
                            func("left")
                        },
                        Modifier
                            .align(Alignment.CenterStart)
                            .background(buttonColor, MaterialTheme.shapes.extraLarge),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowLeft,
                            contentDescription = "Phone", tint = Color(0xFFFF0000)
                        )
                    }

                    IconButton(
                        onClick = {
                                  func("right")
                        },
                        Modifier
                            .align(Alignment.CenterEnd)
                            .background(buttonColor, MaterialTheme.shapes.extraLarge),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = "Phone", tint = Color(0xFFFF0000)
                        )
                    }
                    IconButton(
                        onClick = {
                            func("home")
                        },
                        Modifier
                            .align(Alignment.Center).background(buttonColor, MaterialTheme.shapes.extraLarge)
                        ,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Phone", tint = Color(0xFFFF0000),
                        )
                    }

                    IconButton(
                        onClick = {
                            func("down")
                        },
                        Modifier
                            .align(Alignment.BottomCenter).background(buttonColor, MaterialTheme.shapes.extraLarge)
                            ,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Phone", tint = Color(0xFFFF0000),
                        )
                    }


                }

                Column {
                    Button(
                        onClick = {
                            func("back")
                        },
                        colors = btnColors,
                        modifier = Modifier
                            .width((width / 3).dp)
                            .padding(5.dp)
                            .background(buttonColor, MaterialTheme.shapes.medium),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {

                            Text(text = if (page==0&& !mode){
                                context.resources.getString(R.string.application)
                            }else{
                                context.resources.getString(R.string.delete)
                            }, color = Color(0xFFFF0000))
                        }
                    }
                    Button(
                        onClick = {
                            func("close")
                        },
                        colors = btnColors,
                        modifier = Modifier
                            .width((width / 3).dp)
                            .padding(5.dp)
                            .background(buttonColor, MaterialTheme.shapes.medium),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center

                        ) {
                            //Icon(imageVector = Icons.Filled.Close, contentDescription = "Phone", tint = Color(0xFFFF0000))
                            Text(text = context.resources.getString(R.string.cancel), color = Color(0xFFFF0000))
                        }
                    }
                }
            }


        }
        Column(
            modifier = Modifier
                .width(width.dp)
                .height(newHight.dp)
                .padding(2.dp)
                .background(color = backgroundColor)
        ) {
            for (i in 0..3) {
                Row {
                    for (j in 0..2) {
                        KeyButton(
                            text = list[i][j],
                            subText = subList[i][j],
                            func = {
                                func(list[i][j])
                            },
                            width = width / 3,
                            height = newHight / 4,
                            backgroundColor = buttonColor
                            , fontColor = fontColor
                        )
                    }
                }
            }
        }
    }

    
}
@Preview(showBackground = true)
@Composable
fun KeyButton(
    text: String = "2"
    , subText: String = "ABC"
    , func: () -> Unit = {
        println("click")
    },
    width: Float = 100f,
    height: Float = 50f,
    backgroundColor: Color = Color.DarkGray,
    fontColor: Color = Color.White

              ) {
    Row(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .padding(5.dp)
            .clickable {
                func()
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(0.dp, 0.dp, 0.dp, 5.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .height((height*1/2).dp)
                .padding(0.dp, 0.dp, 0.dp, 0.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = text, fontSize = 25.sp, color = fontColor,
                modifier = Modifier.padding(0.dp, 0.dp, 5.dp, 0.dp),
            )
        }

        Column(
            modifier = Modifier
                .height((height*1/2).dp)
                .padding(0.dp, 0.dp, 0.dp, 0.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {

            Text(text = subText,
                color = fontColor,
                fontSize = 15.sp
            )
        }
    }

}
