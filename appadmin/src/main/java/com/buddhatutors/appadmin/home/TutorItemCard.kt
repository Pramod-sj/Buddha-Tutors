package com.buddhatutors.appadmin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.buddhatutors.domain.model.tutorlisting.TutorListing

@Preview
@Composable
fun PreviewTutorItemCard() {
    com.buddhatutors.common.theme.BuddhaTutorTheme {
        TutorItemCard(
            TutorListing(
                expertiseIn = listOf(
                    Topic("", "Small bhajans, peotry practice"),
                    Topic("", "Test 2"),
                    Topic("", "Test 3")
                ),
                availableDays = listOf(),
                availableTimeSlots = listOf()
            )
        )
    }
}

@Composable
fun TutorItemCard(
    tutorListing: TutorListing,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        onClick = onClick
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .then(
                        if (tutorListing.verification?.isApproved == true) {
                            Modifier.background(Color(0xFF32A72C))
                        } else {
                            Modifier.background(Color(0xFFDF6817))
                        }
                    )
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )


            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp)
            ) {

                Text(
                    text = tutorListing.tutorUser.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Expertise in ${
                        tutorListing.expertiseIn.take(3).map { it.label }.joinToString { it }
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )


            }

        }


    }

}
