package com.buddhatutors.appadmin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.buddhatutors.domain.model.Topic
import com.buddhatutors.domain.model.user.Tutor
import com.buddhatutors.domain.model.user.User

@Preview
@Composable
fun PreviewTutorItemCard() {
    com.buddhatutors.common.theme.BuddhaTutorTheme {
        TutorItemCard(
            Tutor(
                id = "",
                name = "Pramod Singh",
                email = "",
                expertiseIn = listOf(
                    Topic("", "Small bhajans, peotry practice"),
                    Topic("", "Test 2"),
                    Topic("", "Test 3")
                ),
                availabilityDay = listOf(),
                timeAvailability = null
            )
        )
    }
}

@Composable
fun TutorItemCard(
    tutor: Tutor,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium
                    )
            )

            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp)
            ) {

                Text(
                    text = tutor.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "I'm a pass out willing to teach student who are super interested in getting the knowlege of ...",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

            }


        }

    }

}
