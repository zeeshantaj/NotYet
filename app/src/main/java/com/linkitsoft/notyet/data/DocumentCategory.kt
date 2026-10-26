package com.linkitsoft.notyet.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import com.linkitsoft.notyet.R

enum class DocumentCategory(
    @StringRes val titleResId: Int,
    val icon: ImageVector,
) {
    PASSPORT(R.string.category_passport, Icons.Default.Badge),
    VISA(R.string.category_visa, Icons.Default.Public),
    ID_CARD(R.string.category_id_card, Icons.Default.AccountBox),
    LICENSE(R.string.category_license, Icons.Default.DirectionsCar),
    INSURANCE(R.string.category_insurance, Icons.Default.Security),
    WARRANTY(R.string.category_warranty, Icons.Default.VerifiedUser),
    SUBSCRIPTION(R.string.category_subscription, Icons.Default.Subscriptions),
    MEDICINE(R.string.category_medicine, Icons.Default.MedicalServices),
}
