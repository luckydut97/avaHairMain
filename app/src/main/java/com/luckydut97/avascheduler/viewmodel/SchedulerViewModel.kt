package com.luckydut97.avascheduler.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.luckydut97.avascheduler.model.CalendarState
import com.luckydut97.avascheduler.model.DailySchedule
import com.luckydut97.avascheduler.model.Designer
import com.luckydut97.avascheduler.model.DesignerSchedule
import com.luckydut97.avascheduler.model.ScheduleTimeSlot
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID

class SchedulerViewModel : ViewModel() {
    // SharedPreferences 객체
    private lateinit var prefs: SharedPreferences

    // 날짜 포맷터
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // 달력 상태
    var calendarState by mutableStateOf(CalendarState())
        private set

    // 선택된 탭 인덱스
    var selectedTabIndex by mutableStateOf(0)
        private set

    // 디자이너 목록 (초기값 없이 시작)
    var designers by mutableStateOf<List<Designer>>(emptyList())
        private set

    // 모든 스케줄
    var schedules by mutableStateOf<Map<LocalDate, DailySchedule>>(emptyMap())
        private set

    // 휴무일 설정
    private var vacationDates by mutableStateOf<Map<String, List<LocalDate>>>(emptyMap())

    // 에이바헤어 이름
    var salonName by mutableStateOf("에이바헤어")
        private set

    // 스케줄 생성 필요 여부 플래그
    var needScheduleGeneration by mutableStateOf(false)
        private set

    // 디자이너별 순번 카운트 (공평한 배치를 위한 데이터)
    private var designerOrderCounts = mutableMapOf<String, MutableMap<Int, Int>>()  // designerId -> (순번 -> 횟수)

    /**
     * 앱 컨텍스트 초기화
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences("AvaScheduler", Context.MODE_PRIVATE)
        loadDesigners()
        loadVacationDates()
        loadDesignerOrderCounts()

        // 초기화 시 빈 상태로 시작
        schedules = emptyMap()
    }

    /**
     * 날짜 선택 처리
     */
    fun selectDate(date: LocalDate) {
        calendarState = calendarState.copy(selectedDate = date)

        // 다른 달의 날짜를 선택한 경우 해당 달로 이동
        if (date.month != calendarState.currentMonth.month) {
            changeMonth(YearMonth.of(date.year, date.month))
        }
    }

    /**
     * 월 변경 처리
     */
    fun changeMonth(yearMonth: YearMonth) {
        calendarState = CalendarState(
            currentMonth = yearMonth,
            selectedDate = calendarState.selectedDate,
            todayDate = calendarState.todayDate
        )

        // 새로운 월로 변경 시 해당 월의 스케줄 초기화
        initializeSchedulesForMonth(yearMonth)

        // 월 변경시 스케줄 생성 필요 상태로 설정
        needScheduleGeneration = true
    }

    /**
     * 다음 달로 이동
     */
    fun nextMonth() {
        changeMonth(calendarState.currentMonth.plusMonths(1))
    }

    /**
     * 이전 달로 이동
     */
    fun previousMonth() {
        changeMonth(calendarState.currentMonth.minusMonths(1))
    }

    /**
     * 탭 선택 처리
     */
    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    /**
     * 특정 날짜의 디자이너 스케줄 가져오기
     */
    fun getSchedulesForDate(date: LocalDate): List<DesignerSchedule> {
        return schedules[date]?.designerSchedules ?: emptyList()
    }

    /**
     * 새 디자이너/인턴 추가
     */
    fun addDesigner(name: String, color: Color, isIntern: Boolean) {
        val newDesigner = Designer(
            id = UUID.randomUUID().toString(),
            name = name,
            isIntern = isIntern,
            color = color
        )

        designers = designers + newDesigner
        saveDesigners()

        // 새 디자이너 순번 카운트 초기화
        designerOrderCounts[newDesigner.id] = mutableMapOf()

        for (i in 0 until designers.size) {
            designerOrderCounts[newDesigner.id]?.put(i, 0)
        }

        saveDesignerOrderCounts()

        // 스케줄 생성 필요 상태로 설정
        needScheduleGeneration = true
    }

    /**
     * 디자이너 휴무일 설정
     */
    fun setVacationDates(designer: Designer, dates: List<LocalDate>) {
        // 현재 휴무일 목록 업데이트
        vacationDates = vacationDates + (designer.id to dates)

        // 휴무일 저장
        saveVacationDates()

        // 휴무일 설정 후 스케줄 생성 필요 상태로 설정
        needScheduleGeneration = true
    }

    /**
     * 초기 스케줄 생성 - 새로운 디자이너를 위한 기본 근무 패턴 생성
     */
    private fun initializeSchedulesForMonth(yearMonth: YearMonth) {
        // 현재 선택된 월에 대한 스케줄 초기화
        val schedulesMap = schedules.toMutableMap()
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        // 해당 월의 모든 날짜에 대해 빈 스케줄 생성
        var currentDate = firstDay
        while (currentDate.isBefore(lastDay) || currentDate.isEqual(lastDay)) {
            if (!schedulesMap.containsKey(currentDate)) {
                schedulesMap[currentDate] = DailySchedule(date = currentDate, designerSchedules = emptyList())
            }
            currentDate = currentDate.plusDays(1)
        }

        schedules = schedulesMap
    }

    /**
     * 디자이너 삭제
     */
    fun deleteDesigner(designerId: String) {
        // 디자이너 목록에서 삭제
        designers = designers.filter { it.id != designerId }
        saveDesigners()

        // 스케줄에서도 해당 디자이너의 모든 일정 삭제
        val updatedSchedules = schedules.toMutableMap()

        for ((date, dailySchedule) in updatedSchedules) {
            val updatedDesignerSchedules = dailySchedule.designerSchedules.filter {
                it.designer.id != designerId
            }

            updatedSchedules[date] = dailySchedule.copy(designerSchedules = updatedDesignerSchedules)
        }

        schedules = updatedSchedules

        // 휴무일 설정에서도 삭제
        vacationDates = vacationDates.filterKeys { it != designerId }
        saveVacationDates()

        // 순번 카운트에서도 삭제
        designerOrderCounts.remove(designerId)
        saveDesignerOrderCounts()

        // 스케줄 생성 필요 상태로 설정
        needScheduleGeneration = true
    }

    /**
     * 공평한 순번 기반 스케줄 자동 생성
     */
    fun generateSchedules() {
        val currentMonth = calendarState.currentMonth
        val firstDay = currentMonth.atDay(1)
        val lastDay = currentMonth.atEndOfMonth()

        // 스케줄 초기화
        val updatedSchedules = mutableMapOf<LocalDate, DailySchedule>()

        // 이번 달 순번 카운트 (월별 재사용 방지)
        val monthlyOrderCounts = mutableMapOf<String, MutableMap<Int, Int>>()

        // 모든 디자이너 순번 카운트 초기화 (이번 달)
        designers.forEach { designer ->
            monthlyOrderCounts[designer.id] = mutableMapOf()

            // 전체 순번 카운트 초기화 (새 디자이너 추가되었을 수 있음)
            if (!designerOrderCounts.containsKey(designer.id)) {
                designerOrderCounts[designer.id] = mutableMapOf()
            }

            // 모든 순번에 대해 초기화
            for (i in 0 until designers.size) {
                monthlyOrderCounts[designer.id]?.put(i, 0)

                if (!designerOrderCounts[designer.id]!!.containsKey(i)) {
                    designerOrderCounts[designer.id]?.put(i, 0)
                }
            }
        }

        // 현재 월의 모든 날짜에 대해 처리
        var currentDate = firstDay
        while (currentDate.isBefore(lastDay) || currentDate.isEqual(lastDay)) {
            // 해당 날짜에 휴무가 아닌 디자이너 목록
            val availableDesigners = designers.filter { designer ->
                !isDesignerOnVacation(designer.id, currentDate)
            }

            val designerSchedules = mutableListOf<DesignerSchedule>()

            if (availableDesigners.isNotEmpty()) {
                // 공평하게 순번 배정
                val sortedDesigners = assignDesignerOrders(availableDesigners, monthlyOrderCounts)

                // 각 디자이너의 순번 카운트 증가
                sortedDesigners.forEachIndexed { index, designer ->
                    // 스케줄에 추가 - 시간대 개념 없이 순번만 표시
                    designerSchedules.add(
                        DesignerSchedule(
                            designer = designer,
                            date = currentDate,
                            timeSlot = ScheduleTimeSlot.ALL_DAY, // 시간대 개념은 없지만 필드는 유지
                            isAvailable = true,
                            note = "순번 ${index + 1}" // 순번 정보
                        )
                    )

                    // 해당 순번 카운트 증가
                    designerOrderCounts[designer.id]?.let { counts ->
                        counts[index] = (counts[index] ?: 0) + 1
                    }

                    monthlyOrderCounts[designer.id]?.let { counts ->
                        counts[index] = (counts[index] ?: 0) + 1
                    }
                }
            }

            // 일정 저장
            updatedSchedules[currentDate] = DailySchedule(
                date = currentDate,
                designerSchedules = designerSchedules
            )

            // 다음 날짜로 이동
            currentDate = currentDate.plusDays(1)
        }

        // 스케줄 업데이트
        schedules = updatedSchedules

        // 순번 카운트 저장
        saveDesignerOrderCounts()

        // 스케줄 생성 완료 상태로 설정
        needScheduleGeneration = false
    }

    /**
     * 디자이너들의 순번 공평하게 배정
     */
    private fun assignDesignerOrders(
        availableDesigners: List<Designer>,
        monthlyCounts: Map<String, MutableMap<Int, Int>>
    ): List<Designer> {
        // 모든 가능한 배치 순서 생성
        val allPossibleOrders = generateAllPossibleOrders(availableDesigners.size)

        // 가장 공평한 순서 찾기
        var bestOrder = allPossibleOrders.first()
        var minStandardDeviation = Double.MAX_VALUE

        for (order in allPossibleOrders) {
            val stdDev = calculateStandardDeviation(availableDesigners, order, monthlyCounts)

            if (stdDev < minStandardDeviation) {
                minStandardDeviation = stdDev
                bestOrder = order
            }
        }

        // 실제 디자이너 리스트에 순서 적용
        return bestOrder.map { index -> availableDesigners[index] }
    }

    /**
     * n명에 대한 모든 가능한 순서 조합 생성 (첫 5개만)
     */
    private fun generateAllPossibleOrders(n: Int): List<List<Int>> {
        val orders = mutableListOf<List<Int>>()

        // n이 작을 경우 모든 순열 생성 (최대 6명까지)
        if (n <= 6) {
            generatePermutations(IntArray(n) { it }, 0, orders)
        } else {
            // n이 크면 적절한 샘플만 생성
            val baseOrder = List(n) { it }
            orders.add(baseOrder)

            // 몇 가지 변형 추가
            for (i in 0 until minOf(20, n)) {
                val shuffled = baseOrder.shuffled()
                orders.add(shuffled)
            }
        }

        return orders.take(30) // 최대 30개 조합으로 제한 (성능 이슈 방지)
    }

    /**
     * 순열 생성 헬퍼 함수
     */
    private fun generatePermutations(arr: IntArray, start: Int, result: MutableList<List<Int>>) {
        if (start == arr.size) {
            result.add(arr.toList())
            return
        }

        for (i in start until arr.size) {
            // 자리 교환
            val temp = arr[start]
            arr[start] = arr[i]
            arr[i] = temp

            // 재귀 호출
            generatePermutations(arr, start + 1, result)

            // 원상복구
            arr[i] = arr[start]
            arr[start] = temp
        }
    }

    /**
     * 특정 배치 순서의 공평성 계산 (표준편차 이용)
     */
    private fun calculateStandardDeviation(
        designers: List<Designer>,
        orderIndices: List<Int>,
        monthlyCounts: Map<String, MutableMap<Int, Int>>
    ): Double {
        val values = mutableListOf<Int>()

        orderIndices.forEachIndexed { i, designerIndex ->
            val designer = designers[designerIndex]

            // 이번 달에 해당 순번으로 배치된 횟수
            val monthlyCount = monthlyCounts[designer.id]?.get(i) ?: 0

            // 누적 횟수
            val totalCount = designerOrderCounts[designer.id]?.get(i) ?: 0

            // 가중치를 두어 이번 달 카운트가 더 중요하게 처리
            values.add(monthlyCount * a + totalCount)
        }

        // 평균 계산
        val mean = values.average()

        // 표준편차 계산
        val sumSquaredDiffs = values.sumOf { (it - mean) * (it - mean) }
        return kotlin.math.sqrt(sumSquaredDiffs / values.size)
    }

    /**
     * 디자이너가 특정 날짜에 휴무인지 확인
     */
    private fun isDesignerOnVacation(designerId: String, date: LocalDate): Boolean {
        return vacationDates[designerId]?.contains(date) ?: false
    }

    /**
     * 디자이너 정보를 SharedPreferences에 저장
     */
    private fun saveDesigners() {
        val jsonArray = JSONArray()
        designers.forEach { designer ->
            val jsonDesigner = JSONObject().apply {
                put("id", designer.id)
                put("name", designer.name)
                put("isIntern", designer.isIntern)
                put("color", designer.color.toArgb())
            }
            jsonArray.put(jsonDesigner)
        }

        prefs.edit().putString("designers", jsonArray.toString()).apply()
    }

    /**
     * 디자이너 정보를 SharedPreferences에서 불러오기
     */
    private fun loadDesigners() {
        val designersJson = prefs.getString("designers", null) ?: return

        try {
            val jsonArray = JSONArray(designersJson)
            val designersList = mutableListOf<Designer>()

            for (i in 0 until jsonArray.length()) {
                val designerObj = jsonArray.getJSONObject(i)
                val designer = Designer(
                    id = designerObj.getString("id"),
                    name = designerObj.getString("name"),
                    isIntern = designerObj.getBoolean("isIntern"),
                    color = Color(designerObj.getInt("color"))
                )
                designersList.add(designer)
            }

            designers = designersList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 휴무일 정보를 SharedPreferences에 저장
     */
    private fun saveVacationDates() {
        val jsonObject = JSONObject()

        vacationDates.forEach { (designerId, dates) ->
            val datesArray = JSONArray()
            dates.forEach { date ->
                datesArray.put(date.format(dateFormatter))
            }
            jsonObject.put(designerId, datesArray)
        }

        prefs.edit().putString("vacationDates", jsonObject.toString()).apply()
    }

    /**
     * 휴무일 정보를 SharedPreferences에서 불러오기
     */
    private fun loadVacationDates() {
        val vacationJson = prefs.getString("vacationDates", null) ?: return

        try {
            val jsonObject = JSONObject(vacationJson)
            val vacationMap = mutableMapOf<String, List<LocalDate>>()

            val keysIterator = jsonObject.keys()
            while (keysIterator.hasNext()) {
                val designerId = keysIterator.next()
                val datesArray = jsonObject.getJSONArray(designerId)
                val datesList = mutableListOf<LocalDate>()

                for (i in 0 until datesArray.length()) {
                    val dateStr = datesArray.getString(i)
                    val date = LocalDate.parse(dateStr, dateFormatter)
                    datesList.add(date)
                }

                vacationMap[designerId] = datesList
            }

            vacationDates = vacationMap
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 순번 카운트 저장
     */
    private fun saveDesignerOrderCounts() {
        val jsonObject = JSONObject()

        designerOrderCounts.forEach { (designerId, counts) ->
            val countsObj = JSONObject()
            counts.forEach { (order, count) ->
                countsObj.put(order.toString(), count)
            }
            jsonObject.put(designerId, countsObj)
        }

        prefs.edit().putString("designerOrderCounts", jsonObject.toString()).apply()
    }

    /**
     * 순번 카운트 불러오기
     */
    private fun loadDesignerOrderCounts() {
        val countsJson = prefs.getString("designerOrderCounts", null) ?: return

        try {
            val jsonObject = JSONObject(countsJson)

            val designerKeysIterator = jsonObject.keys()
            while (designerKeysIterator.hasNext()) {
                val designerId = designerKeysIterator.next()
                val countsObj = jsonObject.getJSONObject(designerId)

                val orderCounts = mutableMapOf<Int, Int>()
                val orderKeysIterator = countsObj.keys()
                while (orderKeysIterator.hasNext()) {
                    val orderKey = orderKeysIterator.next()
                    val order = orderKey.toInt()
                    val count = countsObj.getInt(orderKey)
                    orderCounts[order] = count
                }

                designerOrderCounts[designerId] = orderCounts
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 오류 발생 시 초기화
            designerOrderCounts.clear()
            designers.forEach { designer ->
                val counts = mutableMapOf<Int, Int>()
                for (i in 0 until designers.size) {
                    counts[i] = 0
                }
                designerOrderCounts[designer.id] = counts
            }
        }
    }

    companion object {
        // 이번 달 순번과 누적 순번 간의 가중치 (이번 달이 3배 더 중요)
        private const val a = 3
    }
}