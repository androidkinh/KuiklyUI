/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI/blob/main/LICENSE
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tencent.kuikly.demo.pages.compose

import androidx.compose.runtime.*
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.*
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.*
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.annotations.Page

// ========== 数据模型 ==========

/** 模拟模型列表条目 */
internal data class ModelItem(
    val id: String,
    val displayName: String,
    val supportsThinking: Boolean = false,
)

/** 聊天消息类型 */
internal enum class MessageRole { USER, ASSISTANT, SYSTEM }

/** 聊天消息数据 */
internal data class ChatMessage(
    val id: Int,
    val role: MessageRole,
    val content: String,
    val timestamp: String = "",
    val isThinking: Boolean = false,
    val codeBlock: String? = null,
    val imageDescription: String? = null,
    val referenceSources: List<String>? = null,
)

/**
 * 模拟完整 AI 聊天页面 Demo
 *  - 顶部: 模型选择器 + 下拉弹窗
 *  - 中间: 复杂 AI 对话列表 (含代码块、图片描述、引用来源、思考中动画等)
 *  - 底部: 输入栏 + 附件弹窗
 */
@Page("AttachmentSheetDemo")
internal class AttachmentSheetDemo : ComposeContainer() {

    override fun willInit() {
        super.willInit()
        setContent {
            ComposeNavigationBar("AI Chat Demo") {
                ChatPageScreen()
            }
        }
    }

    // ========== 颜色常量 ==========
    private val sheetBgColor = Color(0xFFF2F2F7)
    private val tileBgColor = Color.White
    private val iconImageColor = Color(0xFF007AFF)
    private val iconCameraColor = Color(0xFFFF9500)
    private val iconFileColor = Color(0xFF5856D6)
    private val primaryTextColor = Color(0xFF1C1C1E)
    private val secondaryTextColor = Color(0xFF8E8E93)
    private val dividerColor = Color(0xFFE5E5EA)
    private val chatBgColor = Color(0xFFF5F5F5)
    private val inputBarBgColor = Color.White
    private val userBubbleColor = Color(0xFF007AFF)
    private val aiBubbleColor = Color.White
    private val codeBlockBg = Color(0xFF1E1E1E)
    private val codeBlockText = Color(0xFF9CDCFE)
    private val topBarBgColor = Color.White
    private val accentColor = Color(0xFF007AFF)
    private val checkMarkColor = Color(0xFF34C759)
    private val thinkingDotColor = Color(0xFF8E8E93)

    // ========== 尺寸常量 ==========
    private val tileSize = 94.dp
    private val tileCorner = 12.dp
    private val iconSize = 32.dp
    private val topBarHeight = 52.dp
    private val inputBarHeight = 56.dp
    private val avatarSize = 32.dp

    // ========== 模拟数据 ==========

    private val modelList = listOf(
        ModelItem("gpt4o", "GPT-4o", supportsThinking = true),
        ModelItem("gpt4o_mini", "GPT-4o mini"),
        ModelItem("claude_sonnet", "Claude 3.5 Sonnet", supportsThinking = true),
        ModelItem("deepseek_r1", "DeepSeek-R1", supportsThinking = true),
        ModelItem("gemini_pro", "Gemini 2.0 Pro"),
    )

    private val mockMessages = listOf(
        ChatMessage(
            id = 1,
            role = MessageRole.SYSTEM,
            content = "Today",
            timestamp = "09:30",
        ),
        ChatMessage(
            id = 2,
            role = MessageRole.USER,
            content = "帮我用 Kotlin 写一个冒泡排序算法，要求支持泛型并且是稳定排序。",
            timestamp = "09:31",
        ),
        ChatMessage(
            id = 3,
            role = MessageRole.ASSISTANT,
            content = "好的，这是一个支持泛型的稳定冒泡排序实现：",
            timestamp = "09:31",
            codeBlock = """fun <T : Comparable<T>> bubbleSort(list: MutableList<T>) {
    val n = list.size
    for (i in 0 until n - 1) {
        var swapped = false
        for (j in 0 until n - i - 1) {
            if (list[j] > list[j + 1]) {
                val temp = list[j]
                list[j] = list[j + 1]
                list[j + 1] = temp
                swapped = true
            }
        }
        if (!swapped) break // 优化: 无交换则提前退出
    }
}""",
            referenceSources = listOf(
                "Kotlin Official Docs - Generics",
                "Algorithm Design Manual - Ch.4",
            ),
        ),
        ChatMessage(
            id = 4,
            role = MessageRole.USER,
            content = "时间复杂度和空间复杂度分别是多少？能不能和快排做个对比？",
            timestamp = "09:32",
        ),
        ChatMessage(
            id = 5,
            role = MessageRole.ASSISTANT,
            content = """冒泡排序 vs 快速排序对比：

【冒泡排序】
  • 最好情况: O(n) — 数组已有序时
  • 平均情况: O(n²)
  • 最坏情况: O(n²)
  • 空间复杂度: O(1) 原地排序
  • 稳定性: 稳定排序

【快速排序】
  • 最好情况: O(n log n)
  • 平均情况: O(n log n)
  • 最坏情况: O(n²) — 极端 pivot 选择
  • 空间复杂度: O(log n) 递归栈
  • 稳定性: 不稳定排序

总结: 冒泡排序适合小数据量或近乎有序的数据；快排适合大规模随机数据。如果需要稳定排序且数据量大，建议使用归并排序 O(n log n)。""",
            timestamp = "09:33",
        ),
        ChatMessage(
            id = 6,
            role = MessageRole.USER,
            content = "帮我画一个排序算法的选择决策流程图描述。",
            timestamp = "09:34",
        ),
        ChatMessage(
            id = 7,
            role = MessageRole.ASSISTANT,
            content = "这是排序算法选择决策流程的描述：",
            timestamp = "09:34",
            imageDescription = "[流程图] 排序算法决策树\n┌─ 数据量 < 50? ─── Yes ──→ 插入排序\n│                    No\n├─ 需要稳定排序? ─ Yes ──→ 归并排序 / TimSort\n│                    No\n├─ 内存受限? ───── Yes ──→ 堆排序 (原地 O(1))\n│                    No\n└─ 通用场景 ──────────────→ 快速排序 (Dual-Pivot)",
        ),
        ChatMessage(
            id = 8,
            role = MessageRole.USER,
            content = "现在帮我写一个 Compose UI 的 LazyColumn 列表，每个 item 显示排序算法名称、时间复杂度和一个图标。",
            timestamp = "09:36",
        ),
        ChatMessage(
            id = 9,
            role = MessageRole.ASSISTANT,
            content = "好的，以下是完整的 Compose 列表实现：",
            timestamp = "09:36",
            codeBlock = """data class SortAlgorithm(
    val name: String,
    val timeComplexity: String,
    val icon: String,
)

@Composable
fun SortAlgorithmList() {
    val algorithms = remember {
        listOf(
            SortAlgorithm("冒泡排序", "O(n²)", "\uD83D\uDCA7"),
            SortAlgorithm("快速排序", "O(n log n)", "\u26A1"),
            SortAlgorithm("归并排序", "O(n log n)", "\uD83D\uDD00"),
            SortAlgorithm("堆排序",   "O(n log n)", "\uD83C\uDFD4"),
            SortAlgorithm("插入排序", "O(n²)", "\uD83D\uDCCC"),
        )
    }
    LazyColumn {
        items(algorithms) { algo ->
            Row(
                Modifier.fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(algo.icon, fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(algo.name, fontWeight = FontWeight.Bold)
                    Text(algo.timeComplexity, color = Color.Gray)
                }
            }
        }
    }
}""",
        ),
        ChatMessage(
            id = 10,
            role = MessageRole.USER,
            content = "这个代码能直接在 KuiklyUI 里运行吗？有什么兼容性问题需要注意的？",
            timestamp = "09:38",
        ),
        ChatMessage(
            id = 11,
            role = MessageRole.ASSISTANT,
            content = """在 KuiklyUI 中运行需要注意以下几点：

1. 导入路径差异 — KuiklyUI 的 Compose 组件在 com.tencent.kuikly.compose 包下，而非 androidx.compose。

2. @Page 注解 — 每个页面需要用 @Page("PageName") 注册，继承 ComposeContainer 并在 willInit() 中调用 setContent {}。

3. Material3 支持 — KuiklyUI 已支持 Material3 的大部分组件（Card, Button, Text, LazyColumn 等），可以直接使用。

4. 平台差异 — 部分 Modifier（如 shadow, blur）在不同平台表现可能有差异，建议实际运行验证。

5. 字体 — FontFamily 自定义字体需要通过 KuiklyUI 的资源系统加载，不能直接用系统字体路径。

整体来说，将上面的代码迁移到 KuiklyUI 只需要修改 import 路径和添加 @Page 注解即可。""",
            timestamp = "09:39",
            referenceSources = listOf(
                "KuiklyUI Compose 迁移指南",
                "KuiklyUI GitHub - compose module",
                "Material3 兼容性矩阵",
            ),
        ),
        ChatMessage(
            id = 12,
            role = MessageRole.ASSISTANT,
            content = "",
            timestamp = "",
            isThinking = true,
        ),
    )

    // ========== 主界面 ==========

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatPageScreen() {
        var showAttachmentSheet by remember { mutableStateOf(false) }
        var showModelPicker by remember { mutableStateOf(false) }
        var selectedModel by remember { mutableStateOf(modelList[0]) }
        var adaptiveThinking by remember { mutableStateOf(false) }
        var toastMessage by remember { mutableStateOf<String?>(null) }
        val listState = rememberLazyListState()

        // 自动滚到底部
        LaunchedEffect(Unit) {
            listState.animateScrollToItem(mockMessages.size - 1)
        }

        Box(modifier = Modifier.fillMaxSize().background(chatBgColor)) {

            Column(modifier = Modifier.fillMaxSize()) {

                // ==========================================
                // ===== 顶部栏: 模型选择器 =====
                // ==========================================
                TopBar(
                    selectedModel = selectedModel,
                    onClick = { showModelPicker = true },
                )

                // ==========================================
                // ===== 中间: 聊天消息列表 =====
                // ==========================================
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 12.dp, end = 12.dp,
                        top = 8.dp, bottom = 8.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(mockMessages, key = { it.id }) { message ->
                        when (message.role) {
                            MessageRole.SYSTEM -> SystemTimestamp(message)
                            MessageRole.USER -> UserMessageBubble(message)
                            MessageRole.ASSISTANT -> {
                                if (message.isThinking) {
                                    ThinkingIndicator()
                                } else {
                                    AiMessageBubble(message)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // ===== 底部输入栏 =====
                // ==========================================
                InputBar(onPlusClick = { showAttachmentSheet = true })
            }

            // ===== Toast =====
            if (toastMessage != null) {
                LaunchedEffect(toastMessage) {
                    kotlinx.coroutines.delay(1500)
                    toastMessage = null
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC333333))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Text(text = toastMessage ?: "", color = Color.White, fontSize = 14.sp)
                }
            }

            // ==========================================
            // ===== 模型选择弹窗 =====
            // ==========================================
            ModalBottomSheet(
                visible = showModelPicker,
                onDismissRequest = { showModelPicker = false },
                containerColor = sheetBgColor,
                dismissOnDrag = true,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 24.dp)
                    .clip(RoundedCornerShape(14.dp)),
            ) {
                ModelPickerContent(
                    models = modelList,
                    selectedModel = selectedModel,
                    adaptiveThinking = adaptiveThinking,
                    onModelSelected = { model ->
                        selectedModel = model
                        if (!model.supportsThinking) adaptiveThinking = false
                        toastMessage = "已切换: ${model.displayName}"
                    },
                    onThinkingChanged = { enabled ->
                        adaptiveThinking = enabled
                        toastMessage = if (enabled) "深度思考: 已开启" else "深度思考: 已关闭"
                    },
                    onDismiss = { showModelPicker = false },
                )
            }

            // ==========================================
            // ===== 附件选择弹窗 =====
            // ==========================================
            ModalBottomSheet(
                visible = showAttachmentSheet,
                onDismissRequest = { showAttachmentSheet = false },
                containerColor = sheetBgColor,
                dismissOnDrag = true,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 24.dp)
                    .clip(RoundedCornerShape(14.dp)),
            ) {
                AttachmentSheetContent(
                    onDismiss = { showAttachmentSheet = false },
                    onToast = { msg ->
                        showAttachmentSheet = false
                        toastMessage = msg
                    },
                )
            }
        }
    }

    // ==========================================
    //  顶部栏组件
    // ==========================================

    @Composable
    fun TopBar(selectedModel: ModelItem, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .background(topBarBgColor),
        ) {
            // 左侧菜单按钮
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF2F2F7))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {},
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\u2261", fontSize = 18.sp, color = primaryTextColor)
            }

            // 中间: 模型名称 + 箭头
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = selectedModel.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryTextColor,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "\u25BE", // ▾ 下三角
                    fontSize = 12.sp,
                    color = secondaryTextColor,
                )
            }

            // 右侧新建对话按钮
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF2F2F7))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {},
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "+", fontSize = 18.sp, fontWeight = FontWeight.Light, color = primaryTextColor)
            }

            // 底部分割线
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(dividerColor),
            )
        }
    }

    // ==========================================
    //  模型选择弹窗内容
    // ==========================================

    @Composable
    fun ModelPickerContent(
        models: List<ModelItem>,
        selectedModel: ModelItem,
        adaptiveThinking: Boolean,
        onModelSelected: (ModelItem) -> Unit,
        onThinkingChanged: (Boolean) -> Unit,
        onDismiss: () -> Unit,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 标题栏
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                // 关闭按钮
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E5EA))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\u2715", fontSize = 13.sp, color = secondaryTextColor) // ✕
                }
                // 标题
                Text(
                    text = "选择模型",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryTextColor,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 模型列表
            models.forEach { model ->
                val isSelected = model.id == selectedModel.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onModelSelected(model) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 模型图标
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    model.id.contains("gpt") -> Color(0xFF10A37F)
                                    model.id.contains("claude") -> Color(0xFFD97706)
                                    model.id.contains("deepseek") -> Color(0xFF4F46E5)
                                    model.id.contains("gemini") -> Color(0xFF4285F4)
                                    else -> Color(0xFF6B7280)
                                }
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = model.displayName.first().toString(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 模型名称
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = model.displayName,
                            fontSize = 17.sp,
                            color = primaryTextColor,
                        )
                        if (model.supportsThinking) {
                            Text(
                                text = "支持深度思考",
                                fontSize = 12.sp,
                                color = secondaryTextColor,
                            )
                        }
                    }

                    // 选中勾选
                    if (isSelected) {
                        Text(
                            text = "\u2713", // ✓
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = checkMarkColor,
                        )
                    }
                }
            }

            // 分割线
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .height(0.5.dp)
                    .background(dividerColor),
            )

            // 深度思考开关
            val thinkingSupported = selectedModel.supportsThinking
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (thinkingSupported) Color(0xFFFF9500) else Color(0xFFD1D5DB)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\uD83D\uDCA1", fontSize = 18.sp) // 💡
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "深度思考",
                        fontSize = 17.sp,
                        color = if (thinkingSupported) primaryTextColor else secondaryTextColor,
                    )
                    Text(
                        text = if (thinkingSupported) "模型会进行更深入的推理" else "当前模型不支持",
                        fontSize = 12.sp,
                        color = secondaryTextColor,
                    )
                }
                Switch(
                    checked = adaptiveThinking && thinkingSupported,
                    onCheckedChange = { if (thinkingSupported) onThinkingChanged(it) },
                    enabled = thinkingSupported,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ==========================================
    //  聊天消息组件
    // ==========================================

    /** 系统时间戳 */
    @Composable
    fun SystemTimestamp(msg: ChatMessage) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = msg.content,
                fontSize = 12.sp,
                color = secondaryTextColor,
            )
        }
    }

    /** 用户消息气泡 */
    @Composable
    fun UserMessageBubble(msg: ChatMessage) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End,
            ) {
                // 气泡
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(
                            topStart = 16.dp, topEnd = 4.dp,
                            bottomStart = 16.dp, bottomEnd = 16.dp,
                        ))
                        .background(userBubbleColor)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = msg.content,
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // 头像
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(Color(0xFF007AFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "U", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            // 时间戳
            if (msg.timestamp.isNotEmpty()) {
                Text(
                    text = msg.timestamp,
                    fontSize = 10.sp,
                    color = secondaryTextColor,
                    modifier = Modifier.padding(top = 4.dp, end = 44.dp),
                )
            }
        }
    }

    /** AI 回复消息气泡 (支持代码块、图片描述、引用来源) */
    @Composable
    fun AiMessageBubble(msg: ChatMessage) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(verticalAlignment = Alignment.Top) {
                // AI 头像
                Box(
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(Color(0xFF10A37F)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "AI", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                // 气泡内容
                Column(
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .clip(RoundedCornerShape(
                            topStart = 4.dp, topEnd = 16.dp,
                            bottomStart = 16.dp, bottomEnd = 16.dp,
                        ))
                        .background(aiBubbleColor)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    // 文字内容
                    if (msg.content.isNotEmpty()) {
                        Text(
                            text = msg.content,
                            color = primaryTextColor,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                        )
                    }

                    // 代码块
                    if (msg.codeBlock != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(codeBlockBg)
                                .padding(12.dp),
                        ) {
                            Column {
                                // 代码块头部
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(text = "Kotlin", fontSize = 11.sp, color = Color(0xFF858585))
                                    Text(text = "Copy", fontSize = 11.sp, color = accentColor)
                                }
                                // 分割线
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(0.5.dp)
                                        .background(Color(0xFF3E3E3E)),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                // 代码内容
                                Text(
                                    text = msg.codeBlock,
                                    color = codeBlockText,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                )
                            }
                        }
                    }

                    // 图片描述区域
                    if (msg.imageDescription != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0F4F8))
                                .padding(12.dp),
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF4A90D9)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(text = "\uD83D\uDDBC", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Generated Image",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF4A90D9),
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = msg.imageDescription,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF374151),
                                )
                            }
                        }
                    }

                    // 引用来源
                    if (!msg.referenceSources.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(0.5.dp)
                                .background(dividerColor),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "References",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = secondaryTextColor,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        msg.referenceSources.forEachIndexed { index, source ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(accentColor),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = source,
                                    fontSize = 12.sp,
                                    color = accentColor,
                                )
                            }
                        }
                    }
                }
            }
            // 时间戳
            if (msg.timestamp.isNotEmpty()) {
                Text(
                    text = msg.timestamp,
                    fontSize = 10.sp,
                    color = secondaryTextColor,
                    modifier = Modifier.padding(top = 4.dp, start = 44.dp),
                )
            }
        }
    }

    /** 思考中指示器 (3 个跳动的点) */
    @Composable
    fun ThinkingIndicator() {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // AI 头像
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(Color(0xFF10A37F)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "AI", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(
                        topStart = 4.dp, topEnd = 16.dp,
                        bottomStart = 16.dp, bottomEnd = 16.dp,
                    ))
                    .background(aiBubbleColor)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(thinkingDotColor),
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    //  底部输入栏
    // ==========================================

    @Composable
    fun InputBar(onPlusClick: () -> Unit) {
        Column {
            // 顶部分割线
            Box(
                modifier = Modifier.fillMaxWidth().height(0.5.dp).background(dividerColor),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputBarHeight)
                    .background(inputBarBgColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // "+" 按钮
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFE5E5EA))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onPlusClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = primaryTextColor,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 输入框
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF2F2F7)),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = "输入消息...",
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 15.sp,
                        color = secondaryTextColor,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 发送按钮
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\u2191", // ↑
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }

    // ==========================================
    //  附件弹窗内容
    // ==========================================

    @Composable
    fun AttachmentSheetContent(
        onDismiss: () -> Unit,
        onToast: (String) -> Unit,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 标题
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "添加到聊天",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = secondaryTextColor,
                )
            }

            // 三个附件选项
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, bottom = 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AttachmentTile(
                    label = "图片", iconText = "\uD83D\uDDBC",
                    iconBgColor = iconImageColor,
                    onClick = { onToast("已选择: 图片 (最多10张)") },
                )
                AttachmentTile(
                    label = "拍照", iconText = "\uD83D\uDCF7",
                    iconBgColor = iconCameraColor,
                    onClick = { onToast("已选择: 拍照") },
                )
                AttachmentTile(
                    label = "文件", iconText = "\uD83D\uDCC4",
                    iconBgColor = iconFileColor,
                    onClick = { onToast("已选择: 文件 (最多9个)") },
                )
            }

            // 分割线
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .height(0.5.dp).background(dividerColor),
            )

            // Skill 行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onToast("已选择: Skill") }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF34C759)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "/", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text(
                    text = "Skill",
                    modifier = Modifier.padding(start = 12.dp),
                    color = primaryTextColor,
                    fontSize = 17.sp,
                )
            }

            // Agent Team 行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF5856D6)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\u2261", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text(
                    text = "Agent team",
                    modifier = Modifier.padding(start = 12.dp).weight(1f),
                    color = primaryTextColor,
                    fontSize = 17.sp,
                )
                var agentTeamEnabled by remember { mutableStateOf(false) }
                Switch(
                    checked = agentTeamEnabled,
                    onCheckedChange = { agentTeamEnabled = it },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ==========================================
    //  附件选项方块
    // ==========================================

    @Composable
    fun AttachmentTile(
        label: String,
        iconText: String,
        iconBgColor: Color,
        onClick: () -> Unit,
    ) {
        Box(
            modifier = Modifier
                .size(tileSize)
                .clip(RoundedCornerShape(tileCorner))
                .background(tileBgColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = iconText, fontSize = 18.sp)
                }
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = primaryTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
