package com.skymilk.chatapp.store.presentation.navigation.bottom

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.R

@Composable
fun MainBottomBar(
    items: List<BottomNavigationItem>,
    selected: Int,
    onItemClick: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 10.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                selected = selected == index,
                onClick = { onItemClick(index) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badge != 0) {
                                Badge{ Text(text = item.badge.toString(), style = MaterialTheme.typography.labelMedium) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selected == index) item.selectedIcon else item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }


                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = colorResource(id = R.color.body),
                    unselectedTextColor = colorResource(id = R.color.body)
                )
            )
        }
    }
}