package com.healthapp.emotional.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.healthapp.emotional.R

@Composable
fun ExploreScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Explorar",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Seção de Meditações
        item {
            ContentSection(
                title = "Meditações Guiadas",
                subtitle = "Encontre paz e equilíbrio",
                items = meditationItems
            )
        }

        // Seção de Podcasts
        item {
            ContentSection(
                title = "Podcasts de Saúde Mental",
                subtitle = "Aprenda com especialistas",
                items = podcastItems
            )
        }

        // Seção de Músicas
        item {
            ContentSection(
                title = "Músicas Relaxantes",
                subtitle = "Sons para acalmar a mente",
                items = musicItems
            )
        }

        // Seção de Artigos
        item {
            ContentSection(
                title = "Artigos e Dicas",
                subtitle = "Conhecimento para seu bem-estar",
                items = articleItems
            )
        }
    }
}

@Composable
fun ContentSection(
    title: String,
    subtitle: String,
    items: List<ContentItem>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                ContentCard(item)
            }
        }
    }
}

@Composable
fun ContentCard(item: ContentItem) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { /* TODO: Implementar ação */ },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay gradiente
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
                
                // Duração ou tipo
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = item.author,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

data class ContentItem(
    val title: String,
    val description: String,
    val imageUrl: String,
    val duration: String,
    val author: String,
    val icon: ImageVector
)

// Dados de exemplo
private val meditationItems = listOf(
    ContentItem(
        title = "Meditação para Ansiedade",
        description = "Uma meditação guiada de 10 minutos para aliviar a ansiedade e encontrar calma interior.",
        imageUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773",
        duration = "10 min",
        author = "Dra. Ana Silva",
        icon = Icons.Default.PlayCircle
    ),
    ContentItem(
        title = "Respiração Consciente",
        description = "Técnicas de respiração para momentos de estresse e ansiedade.",
        imageUrl = "https://images.unsplash.com/photo-1518241353330-0f7941c2d9b5",
        duration = "15 min",
        author = "Prof. Carlos Santos",
        icon = Icons.Default.PlayCircle
    ),
    ContentItem(
        title = "Mindfulness para Iniciantes",
        description = "Aprenda os fundamentos da meditação mindfulness.",
        imageUrl = "https://images.unsplash.com/photo-1508672019048-805c876b67e2",
        duration = "20 min",
        author = "Dra. Maria Oliveira",
        icon = Icons.Default.PlayCircle
    )
)

private val podcastItems = listOf(
    ContentItem(
        title = "Saúde Mental no Trabalho",
        description = "Como manter o equilíbrio emocional no ambiente profissional.",
        imageUrl = "https://images.unsplash.com/photo-1551836022-d5d88e9218df",
        duration = "45 min",
        author = "Psicóloga Julia Costa",
        icon = Icons.Default.Headphones
    ),
    ContentItem(
        title = "Ansiedade e Pandemia",
        description = "Estratégias para lidar com a ansiedade em tempos de isolamento.",
        imageUrl = "https://images.unsplash.com/photo-1576091160550-2173dba999ef",
        duration = "50 min",
        author = "Dr. Pedro Mendes",
        icon = Icons.Default.Headphones
    ),
    ContentItem(
        title = "Autoestima e Autoconfiança",
        description = "Desenvolvendo uma relação saudável consigo mesmo.",
        imageUrl = "https://images.unsplash.com/photo-1517021897933-0e0319cfbc28",
        duration = "40 min",
        author = "Dra. Beatriz Lima",
        icon = Icons.Default.Headphones
    )
)

private val musicItems = listOf(
    ContentItem(
        title = "Sons da Natureza",
        description = "Uma coleção de sons naturais para relaxamento e concentração.",
        imageUrl = "https://images.unsplash.com/photo-1470252649378-9c29740c9fa8",
        duration = "60 min",
        author = "Nature Sounds",
        icon = Icons.Default.MusicNote
    ),
    ContentItem(
        title = "Música Clássica Relaxante",
        description = "Seleção de músicas clássicas para acalmar a mente.",
        imageUrl = "https://images.unsplash.com/photo-1511379938547-c1f69419868d",
        duration = "45 min",
        author = "Classical Relax",
        icon = Icons.Default.MusicNote
    ),
    ContentItem(
        title = "Frequências de Cura",
        description = "Músicas com frequências específicas para relaxamento profundo.",
        imageUrl = "https://images.unsplash.com/photo-1516280440614-37939bbacd81",
        duration = "30 min",
        author = "Healing Frequencies",
        icon = Icons.Default.MusicNote
    )
)

private val articleItems = listOf(
    ContentItem(
        title = "Como Desenvolver Resiliência",
        description = "Aprenda a lidar com desafios e se recuperar de adversidades.",
        imageUrl = "https://images.unsplash.com/photo-1507413245164-6160d8298b31",
        duration = "5 min",
        author = "Dr. Ricardo Alves",
        icon = Icons.Default.Article
    ),
    ContentItem(
        title = "Hábitos para Saúde Mental",
        description = "Pequenas mudanças que fazem grande diferença no seu bem-estar.",
        imageUrl = "https://images.unsplash.com/photo-1499209974431-9dddcece7f88",
        duration = "7 min",
        author = "Dra. Camila Santos",
        icon = Icons.Default.Article
    ),
    ContentItem(
        title = "Sono e Saúde Mental",
        description = "A importância do sono para o equilíbrio emocional.",
        imageUrl = "https://images.unsplash.com/photo-1511295742362-92c96b1cf484",
        duration = "6 min",
        author = "Dr. Lucas Oliveira",
        icon = Icons.Default.Article
    )
) 