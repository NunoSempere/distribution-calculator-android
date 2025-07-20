package com.nunosempere.distributioncalculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tips") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
			Text("- The boxes represent the 90% confidence interval (5% to 95%) of your distributions")
			Text("- The app tries to be a bit smart around forcing the 95%-ile value to be larger than")
			Text("  or equal to the 5%-ile value by duplicating it when it is smaller")
			Text("- Swipe left or right to duplicate an input")
			Text("")
			Text("Try this example estimating the amount of fat burnt by going down and coming back up from the Grand canyon:")
			Text("300 600, meters going down")
			Text("× 98 105, kgs of weight")
			Text("× 1.3 2, going down, and then going back up the Canyon, but down is easier")
			Text("× 9.8 gravitational constant to get to Jules spent")
			Text("÷ 15% 35%, wild guess on chemical to physical efficiency conversion")
			Text("÷ 70% 90%, wild guess for share of loss that is due to the direct physical exercise")
			Text("(as opposed to higher burn rate, cost of building muscles, etc.)")
			Text("÷ 4, jules to calories")
			Text("÷ 6K to 8K, calories burnt to lose one gram of fat")
			Text("= ¿ to ? grams of fat lost")
			Text("")
			Text("You can make suggestions at <https://github.com/NunoSempere/distribution-calculator-android>")
			Text("You can also find this link in the F-Droid page for this app")
        }
    }
}
