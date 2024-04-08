package pers.cierra_runis.terra.ui.pages

import SvgIcon
import android.*
import android.annotation.*
import android.content.*
import android.content.pm.*
import android.location.*
import android.util.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.core.app.*
import com.qweather.sdk.bean.weather.*
import com.qweather.sdk.view.QWeather.*
import kotlinx.coroutines.*
import pers.cierra_runis.terra.ui.*
import java.time.*
import java.time.format.*


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(snackbarHostState: SnackbarHostState) {

    val navController = LocalNavController.current
    var location by remember {
        mutableStateOf<Location?>(null)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Log.d("哇袄！", "测你码")

    SideEffect {
        if (context.canGetPosition()) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } else {
            Log.d("Terra", "无法获取位置")

            scope.launch {
                snackbarHostState.showSnackbar("无法获取位置")
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "地名")
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("Settings")
                    }) {
                        Icon(Icons.Rounded.Settings, "Settings")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        if (location != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                item {
                    WeatherNowCard(location!!, snackbarHostState)
                }
                item {
                    Weather24HourCard(location!!, snackbarHostState)
                }
                item {
                    Weather7DayList(location!!, snackbarHostState)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun WeatherNowCard(location: Location, snackbarHostState: SnackbarHostState) {

    val context = LocalContext.current
    var weatherNowBean by remember {
        mutableStateOf<WeatherNowBean?>(null)
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(location) {
        getWeatherNow(
            context,
            "${location.longitude},${location.latitude}",
            object : OnResultWeatherNowListener {
                override fun onError(p0: Throwable) {
                    Log.d("Terra", "天气获取失败 $p0")
                    scope.launch { snackbarHostState.showSnackbar(message = "天气获取失败") }
                }

                override fun onSuccess(p0: WeatherNowBean) {
                    weatherNowBean = p0
                    scope.launch { snackbarHostState.showSnackbar(message = "天气获取成功") }
                }
            }
        )
    }


    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            ListItem(
                leadingContent = { Icon(Icons.Rounded.Info, null) },
                headlineContent = { Text(text = "当前天气") },
            )

            if (weatherNowBean != null) {
                val now = weatherNowBean!!.now
                ListItem(
                    headlineContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${now.temp} °C",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SvgIcon(
                                path = "drawable/${now.icon}.svg",
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    },
                    supportingContent = {
                        Text(text = "体感 ${now.feelsLike} °C")
                    },
                    trailingContent = {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = now.text,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(text = "降水量 ${now.precip} mm")
                            Text(text = "${now.windDir} ${now.windScale} 级")
                            Text(text = "大气压强 ${now.pressure} 百帕")
                            Text(text = "能见度 ${now.vis} km")
                            Text(text = "云量 ${now.cloud} %")
                            Text(text = "相对湿度 ${now.humidity} %")
                            Text(text = "露点温度 ${now.dew} °C")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Weather24HourCard(location: Location, snackbarHostState: SnackbarHostState) {

    var weatherHourlyBean by remember {
        mutableStateOf<WeatherHourlyBean?>(null)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(location) {
        getWeather24Hourly(
            context, "${location.longitude},${location.latitude}",
            object :
                OnResultWeatherHourlyListener {
                override fun onError(p0: Throwable) {
                    Log.d("Terra", "获取 24 小时天气预报失败 $p0")
                    scope.launch { snackbarHostState.showSnackbar("获取 24 小时天气预报失败") }
                }

                override fun onSuccess(p0: WeatherHourlyBean) {
                    weatherHourlyBean = p0
                }
            },
        )
    }


    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(text = "24 小时天气预报") },
            leadingContent = { Icon(Icons.Rounded.Star, null) },
        )

        if (weatherHourlyBean == null) {
            CircularProgressIndicator()
        } else {
            LazyRow(
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(weatherHourlyBean!!.hourly) { bean ->
                    val time = OffsetDateTime.parse(bean.fxTime)

                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = time.format(DateTimeFormatter.ofPattern("HH:mm")))
                        Spacer(modifier = Modifier.height(8.dp))

                        SvgIcon(
                            path = "drawable/${bean.icon}.svg",
                            contentDescription = bean.text,
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${bean.temp} °C")
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Weather7DayList(location: Location, snackbarHostState: SnackbarHostState) {
    var weatherDailyBean by remember {
        mutableStateOf<WeatherDailyBean?>(null)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(location) {
        getWeather7D(context, "${location.longitude},${location.latitude}", object :
            OnResultWeatherDailyListener {
            override fun onError(p0: Throwable) {
                scope.launch {
                    Log.d("Terra", "获取 7 日天气预报失败 $p0")
                    snackbarHostState.showSnackbar("获取 7 日天气预报失败")
                }
            }

            override fun onSuccess(p0: WeatherDailyBean) {
                weatherDailyBean = p0
            }
        })
    }

    ElevatedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        ListItem(
            leadingContent = { Icon(Icons.Rounded.DateRange, null) },
            headlineContent = { Text(text = "7 日天气预报") },
        )

        if (weatherDailyBean == null) {
            CircularProgressIndicator()
        } else {
            val daily = weatherDailyBean!!.daily
            val tempAllMin = daily.minByOrNull { it.tempMin.toFloat() }!!.tempMin.toFloat()
            val tempAllMax = daily.maxByOrNull { it.tempMax.toFloat() }!!.tempMax.toFloat()

            Column {
                weatherDailyBean!!.daily.map { day ->
                    val time = day.fxDate.split('-')
                    val tempMin = day.tempMin.toFloat()
                    val tempMax = day.tempMax.toFloat()
                    ListItem(
                        leadingContent = { Text(text = time.drop(1).joinToString("/")) },
                        headlineContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SvgIcon(
                                    path = "drawable/${day.iconDay}.svg",
                                    contentDescription = day.textDay
                                )
                                RangeSlider(
                                    value = tempMin..tempMax,
                                    onValueChange = {},
                                    startThumb = {
                                        Text(
                                            text = "${day.tempMin} °C",
                                            modifier = Modifier.offset(y = 16.dp)
                                        )
                                    },
                                    endThumb = {
                                        Text(
                                            text = "${day.tempMax} °C",
                                            modifier = Modifier.offset(y = 16.dp)
                                        )
                                    },
                                    valueRange = tempAllMin - 1..tempAllMax + 1
                                )
                            }
                        },
                        trailingContent = {
                            SvgIcon(
                                path = "drawable/${day.iconNight}.svg",
                                contentDescription = day.textNight
                            )
                        }
                    )
                }
            }
        }
    }
}

fun Context.canGetPosition(): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

