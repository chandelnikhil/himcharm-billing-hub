import { Box, Chip, Typography } from '@mui/material'
import ConstructionRoundedIcon from '@mui/icons-material/ConstructionRounded'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'

export default function ComingSoonPage({ title }) {
  return (
    <>
      <PageHeader title={title} description={`${title} tools are planned for a future release.`} />
      <ModuleCard>
        <Box sx={{ minHeight: 480, display: 'grid', placeItems: 'center', p: 4, textAlign: 'center' }}>
          <Box>
            <Box sx={{ width: 72, height: 72, display: 'grid', placeItems: 'center', mx: 'auto', mb: 2.5, borderRadius: 3, color: 'primary.main', bgcolor: '#e8f3ff' }}><ConstructionRoundedIcon sx={{ fontSize: 34 }} /></Box>
            <Chip label="COMING SOON" size="small" sx={{ color: 'primary.main', bgcolor: '#e8f3ff', fontWeight: 800, letterSpacing: '.08em', fontSize: 10.5 }} />
            <Typography variant="h5" sx={{ mt: 2 }}>{title} is on the roadmap</Typography>
            <Typography color="text.secondary" sx={{ mt: 1, maxWidth: 430 }}>We’re shaping this module to fit naturally into your store workflow. It will be available in a future update.</Typography>
          </Box>
        </Box>
      </ModuleCard>
    </>
  )
}
