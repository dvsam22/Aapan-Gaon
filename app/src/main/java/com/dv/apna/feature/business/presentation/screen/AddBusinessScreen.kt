package com.dv.apna.feature.business.presentation.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.dv.apna.R
import com.dv.apna.core.theme.MintGradientEnd
import com.dv.apna.core.theme.MintGradientMiddle
import com.dv.apna.core.theme.MintGradientStart
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp
import com.dv.apna.feature.business.presentation.event.AddBusinessEvent
import com.dv.apna.feature.business.presentation.state.AddBusinessState
import java.net.URLEncoder

private val BrandGreen = Color(0xFF38C792)
private val BrandGreenDark = Color(0xFF2CA074)
private val LightGreenBg = Color(0xFFEFFAF6)
private val BorderGray = Color(0xFFD9D9D9)
private val TextDark = Color(0xFF1E232C)
private val TextMuted = Color(0xFF8391A1)

@Composable
fun AddBusinessScreen(
    state: AddBusinessState,
    onEvent: (AddBusinessEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MintGradientStart, MintGradientMiddle, MintGradientEnd)))
    ) {
        val (bottomImage, mainContent) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.iv_bottomview),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomImage) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            AddBusinessTopBar(
                title = stringResource(id = R.string.add_business_title),
                onBackClick = onNavigateBack
            )

            if (state.isSubmittedSuccess) {
                SuccessSubmissionView(
                    state = state,
                    onSendWhatsApp = {
                        sendBusinessViaWhatsApp(context, state)
                    },
                    onGoBack = {
                        onEvent(AddBusinessEvent.ResetForm)
                        onNavigateBack()
                    }
                )
            } else {
                BusinessFormContent(
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun AddBusinessTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.sdp(), vertical = 12.sdp()),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .width(34.sdp())
                .height(44.sdp())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onBackClick() },
            shape = RoundedCornerShape(22.sdp()),
            color = LightGreenBg,
            border = BorderStroke(1.sdp(), BorderGray)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back",
                    modifier = Modifier.size(20.sdp()),
                    tint = Color.Black
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.ssp()
            ),
            color = TextDark,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun BusinessFormContent(
    state: AddBusinessState,
    onEvent: (AddBusinessEvent) -> Unit
) {
    val scrollState = rememberScrollState()

    val categories = remember {
        listOf(
            "फसल व सब्जी खरीदार (Mandi Buyer)",
            "डॉक्टर व क्लिनिक (Doctor / Clinic)",
            "मेडिकल स्टोर (Pharmacy / Medical)",
            "अस्पताल (Hospital)",
            "एम्बुलेंस सेवा (Ambulance)",
            "ईंट भट्ठा सप्लायर (Bricks Supplier)",
            "सीमेंट, बालू व मटेरियल (Building Material)",
            "हार्डवेयर स्टोर (Hardware Shop)",
            "लिन्टर मशीन (Linter Machine)",
            "सेंटरिंग व शटरिंग (Shuttering)",
            "राजमिस्त्री (Rajmistri / Mason)",
            "बिजली मिस्त्री (Electrician)",
            "प्लंबर (Plumber)",
            "बढ़ई (Carpenter)",
            "पेंटर (Painter)",
            "मजदूर / लेबर (Labour)",
            "ट्रैक्टर व ट्रॉली किराया (Tractor Rental)",
            "पिकअप व लोडर वाहन (Pickup / Loader)",
            "जेसीबी मशीन (JCB Machine)",
            "कार व टैक्सी किराया (Car / Taxi)",
            "टेंट हाउस व सजावट (Tent House)",
            "हलवाई व कैटरिंग (Halwai & Catering)",
            "फोटो व वीडियोग्राफी (Photography)",
            "डीजे व बैंड बाजा (DJ Sound & Band)",
            "मैरिज हॉल व वाटिका (Marriage Hall)",
            "अन्य दुकान / सेवा (Other Shop / Service)"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.sdp(), vertical = 8.sdp())
    ) {
        // Intro Banner Card - Matching ServiceCard style
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.sdp()),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.sdp())
                    .background(LightGreenBg, RoundedCornerShape(8.sdp()))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.sdp(), vertical = 12.sdp()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.sdp())
                            .background(Color.White, CircleShape)
                            .border(1.sdp(), BorderGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.local_news),
                            contentDescription = null,
                            modifier = Modifier.size(24.sdp()),
                            tint = BrandGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.width(12.sdp()))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.add_business_service),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.ssp()
                            ),
                            color = BrandGreenDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.sdp()))

                        Text(
                            text = stringResource(id = R.string.add_business_subtitle),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.ssp(),
                                color = TextMuted
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.sdp()))

        // Main Input Fields Card - Matching ServiceCard style
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.sdp()),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.sdp())
                    .background(LightGreenBg, RoundedCornerShape(8.sdp()))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.sdp())
                ) {
                    // 1. Full Name (Single line)
                    FormField(
                        label = stringResource(id = R.string.full_name_label),
                        placeholder = stringResource(id = R.string.full_name_placeholder),
                        value = state.contactName,
                        onValueChange = { onEvent(AddBusinessEvent.ContactNameChanged(it)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.sdp()))

                    // 2. Business Name (Single line)
                    FormField(
                        label = stringResource(id = R.string.business_name_label),
                        placeholder = stringResource(id = R.string.business_name_placeholder),
                        value = state.businessName,
                        onValueChange = { onEvent(AddBusinessEvent.BusinessNameChanged(it)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.sdp()))

                    // 3. Category Dropdown (Single line)
                    CategoryDropdownField(
                        label = stringResource(id = R.string.category_label),
                        placeholder = stringResource(id = R.string.select_category_placeholder),
                        selectedCategory = state.selectedCategory,
                        categories = categories,
                        onCategorySelected = { onEvent(AddBusinessEvent.CategorySelected(it)) }
                    )

                    Spacer(modifier = Modifier.height(12.sdp()))

                    // 4. Mobile Number (Single line)
                    FormField(
                        label = stringResource(id = R.string.mobile_number_label),
                        placeholder = stringResource(id = R.string.mobile_number_placeholder),
                        value = state.phoneNumber,
                        onValueChange = { onEvent(AddBusinessEvent.PhoneNumberChanged(it)) },
                        keyboardType = KeyboardType.Phone,
                        leadingText = "+91 ",
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.sdp()))

                    // 5. Address / Village (Single line)
                    FormField(
                        label = stringResource(id = R.string.address_label),
                        placeholder = stringResource(id = R.string.address_placeholder),
                        value = state.address,
                        onValueChange = { onEvent(AddBusinessEvent.AddressChanged(it)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.sdp()))

                    // 6. Description (Multi line optional)
                    FormField(
                        label = stringResource(id = R.string.description_label),
                        placeholder = stringResource(id = R.string.description_placeholder),
                        value = state.description,
                        onValueChange = { onEvent(AddBusinessEvent.DescriptionChanged(it)) },
                        singleLine = false,
                        minLines = 3
                    )

                    // Error Message Display
                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.sdp()))
                        Text(
                            text = state.errorMessage.asString(),
                            color = Color(0xFFD32F2F),
                            fontSize = 11.ssp(),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.sdp()))

                    // Submit Button - App theme pill shape and color
                    Button(
                        onClick = { onEvent(AddBusinessEvent.Submit) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.sdp()),
                        shape = RoundedCornerShape(27.sdp()),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = Color.White,
                            disabledContainerColor = BrandGreen.copy(alpha = 0.4f),
                            disabledContentColor = Color.White
                        ),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.sdp()),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.sdp()))
                            Text(
                                text = stringResource(id = R.string.submitting),
                                color = Color.White,
                                fontSize = 13.ssp(),
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.submit_business),
                                color = Color.White,
                                fontSize = 14.ssp(),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.sdp()))
    }
}

@Composable
private fun FormField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
    leadingText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.ssp()
            ),
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(5.sdp()))

        val containerModifier = if (singleLine) {
            Modifier
                .fillMaxWidth()
                .height(42.sdp())
                .clip(RoundedCornerShape(10.sdp()))
                .background(Color.White)
                .border(1.sdp(), BorderGray, RoundedCornerShape(10.sdp()))
                .padding(horizontal = 12.sdp())
        } else {
            Modifier
                .fillMaxWidth()
                .heightIn(min = 80.sdp())
                .clip(RoundedCornerShape(10.sdp()))
                .background(Color.White)
                .border(1.sdp(), BorderGray, RoundedCornerShape(10.sdp()))
                .padding(horizontal = 12.sdp(), vertical = 10.sdp())
        }

        Box(
            modifier = containerModifier,
            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
        ) {
            Row(
                modifier = if (singleLine) Modifier.fillMaxSize() else Modifier.fillMaxWidth(),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
            ) {
                if (leadingText != null) {
                    Text(
                        text = leadingText,
                        color = BrandGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.ssp(),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(4.sdp()))
                }

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                    minLines = minLines,
                    cursorBrush = SolidColor(BrandGreen),
                    textStyle = TextStyle(
                        color = TextDark,
                        fontSize = 13.ssp(),
                        fontWeight = FontWeight.Normal
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = TextMuted,
                                    fontSize = 12.ssp(),
                                    maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = !singleLine
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdownField(
    label: String,
    placeholder: String,
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.ssp()
            ),
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(5.sdp()))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.sdp())
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                    .clip(RoundedCornerShape(10.sdp()))
                    .background(Color.White)
                    .border(1.sdp(), BorderGray, RoundedCornerShape(10.sdp()))
                    .padding(horizontal = 12.sdp()),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory.isNotBlank()) selectedCategory else placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 12.ssp(),
                            color = if (selectedCategory.isBlank()) TextMuted else TextDark
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.sdp()))

                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .size(16.sdp())
                            .rotate(if (expanded) 90f else -90f)
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .border(1.sdp(), BrandGreen, RoundedCornerShape(12.sdp())),
                containerColor = Color.White,
                shape = RoundedCornerShape(12.sdp())
            ) {
                categories.forEachIndexed { index, category ->
                    val isSelected = category == selectedCategory
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.ssp(),
                                        color = if (isSelected) BrandGreenDark else TextDark,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.sdp()))

                                // Radio Selection Indicator matching VillageSelector
                                Box(
                                    modifier = Modifier
                                        .size(16.sdp())
                                        .border(1.sdp(), BrandGreen, CircleShape)
                                        .padding(3.sdp())
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(BrandGreen, CircleShape)
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                    if (index < categories.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.sdp()),
                            thickness = 1.sdp(),
                            color = BrandGreen.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessSubmissionView(
    state: AddBusinessState,
    onSendWhatsApp: () -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.sdp(), vertical = 24.sdp()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.sdp()),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.sdp())
                    .background(LightGreenBg, RoundedCornerShape(8.sdp()))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.sdp()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(56.sdp())
                    )

                    Spacer(modifier = Modifier.height(14.sdp()))

                    Text(
                        text = stringResource(id = R.string.business_submit_success_title),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.ssp()
                        ),
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.sdp()))

                    Text(
                        text = stringResource(id = R.string.business_submit_success_desc),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.ssp(),
                            color = TextMuted
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.sdp()))

                    // WhatsApp Button - matching app pill shape
                    Button(
                        onClick = onSendWhatsApp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.sdp()),
                        shape = RoundedCornerShape(27.sdp()),
                        contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.iv_whatsapp),
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(22.sdp())
                            )
                            Spacer(modifier = Modifier.width(8.sdp()))
                            Text(
                                text = stringResource(id = R.string.send_on_whatsapp),
                                color = Color.White,
                                fontSize = 13.ssp(),
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.sdp()))

                    // Home Button - matching app pill shape
                    OutlinedButton(
                        onClick = onGoBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.sdp()),
                        shape = RoundedCornerShape(27.sdp()),
                        border = BorderStroke(1.sdp(), BrandGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandGreen)
                    ) {
                        Text(
                            text = stringResource(id = R.string.go_to_home),
                            color = BrandGreen,
                            fontSize = 13.ssp(),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun sendBusinessViaWhatsApp(context: Context, state: AddBusinessState) {
    val message = """
        *नया व्यवसाय / सेवा पंजीकरण (Aapan Gaon App)*
        👤 नाम: ${state.contactName}
        🏪 दुकान/सेवा का नाम: ${state.businessName}
        📂 श्रेणी: ${state.selectedCategory}
        📞 मोबाइल: ${state.phoneNumber}
        📍 पता/गांव: ${state.address}
        📝 विवरण: ${state.description.ifBlank { "लागू नहीं" }}
    """.trimIndent()

    val adminNumber = "918534865222"
    val encoded = URLEncoder.encode(message, "UTF-8")
    val url = "https://api.whatsapp.com/send?phone=$adminNumber&text=$encoded"
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(sendIntent, "WhatsApp"))
    }
}
