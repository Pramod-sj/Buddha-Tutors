package com.buddhatutors.feature.admin.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.buddhatutors.common.shimmerBrush

@Composable
fun TutorItemCardPlaceholder() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .background(
                    brush = shimmerBrush(),
                    shape = CircleShape
                )
        )


        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(end = 8.dp)
        ) {

            Text(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(
                        brush = shimmerBrush(),
                        shape = CircleShape
                    ),
                text = ""
            )

            Spacer(Modifier.height(4.dp))

            Text(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(
                        brush = shimmerBrush(),
                        shape = CircleShape
                    ),
                text = ""
            )


        }

    }

}
