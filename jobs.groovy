import jenkins.model.*
import hudson.model.*

def now = new Date()

def buildingJobs = Jenkins.instance.getAllItems(Job.class).findAll {
  it.isBuilding()
}

properties(
    [
        pipelineTriggers([cron('* * * * *')]),
    ]
)

buildingJobs.each { job ->
  allRuns = job.getBuilds()

  allRuns.each { item ->
    def duration
    String buildingStatus = ""

    if (item.isBuilding()) {
      buildingStatus = " (RUNNING)"
      duration = now.getTime() - item.getStartTimeInMillis()
    } else {
      duration = item.getDuration()
    }

    def durationMin = (duration / 60000).intValue()
    def estDuration = item.getEstimatedDuration()
    def estDurationMin = (estDuration / 60000).intValue()
    String jobname = item.getUrl()

    String status = "OK"
    if (estDuration < duration) {
      status = "KO"
      double d = duration / estDuration
      if (d.round() > 3) {
        status = "!KO"
      }
    }

    println "[${status}${buildingStatus}] ${jobname} Current: ${durationMin}min. Estimated: ${estDurationMin}min."
  }
}
