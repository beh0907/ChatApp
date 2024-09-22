import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skymilk.chatapp.ui.theme.HannaPro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetImagePicker(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onImagePicker: () -> Unit,
    onCameraCapture: () -> Unit,
) {
    if (isVisible) {
        val bottomSheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomSheetOption(
                    icon = Icons.Default.Image,
                    text = "이미지 가져오기",
                    onClick = {
                        onImagePicker()
                        onDismiss()
                    }
                )

                BottomSheetOption(
                    icon = Icons.Default.CameraAlt,
                    text = "카메라 촬영",
                    onClick = {
                        onCameraCapture()
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetOption(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null)

        Spacer(Modifier.size(16.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = HannaPro
        )
    }
}