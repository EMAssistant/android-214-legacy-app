package com.randomvoids.emassistant.util.icsforms.ics214

import com.randomvoids.emassistant.model.ICS214WithActivityLogAndResources
import com.randomvoids.emassistant.model.Incident
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class ICS214Saver {
    companion object {
        @Suppress("BlockingMethodInNonBlockingContext")
        fun saveICS214ToPDF(onSuccess: () -> Unit, onError: (String) -> Unit, incident: Incident, userCompleteICS214: ICS214WithActivityLogAndResources, signatureFile: File, ics214InputStream: InputStream, fileOutputStream: OutputStream) {
            val userICS214 = userCompleteICS214.ics214Details
            val ics214PDf = PDDocument.load(ics214InputStream)
            val ics214acroform = ics214PDf.documentCatalog.acroForm
            (ics214acroform.getField("1 Incident Name_19") as PDTextField).value = incident.incidentName
            (ics214acroform.getField(ICS214PDFFieldNames.TEAM_NAME) as PDTextField).value = userICS214.teamName
            (ics214acroform.getField(ICS214PDFFieldNames.TEAM_ICS_POSITION) as PDTextField).value = userICS214.teamIcsPosition
            (ics214acroform.getField(ICS214PDFFieldNames.HOME_AGENCY) as PDTextField).value = userICS214.homeAgency
            (ics214acroform.getField(ICS214PDFFieldNames.PREPARED_BY_NAME) as PDTextField).value = userICS214.preparedByName
            (ics214acroform.getField(ICS214PDFFieldNames.PREPARED_BY_POSITION_TITLE) as PDTextField).value = userICS214.preparedByPositionTitle
            (ics214acroform.getField(ICS214PDFFieldNames.PREPARED_BY_DATE_TIME) as PDTextField).value = userICS214.getFormattedPreparedByDateTime()
            (ics214acroform.getField("1 Incident Name_20") as PDTextField).value = incident.incidentName
            (ics214acroform.getField(ICS214PDFFieldNames.DATE_FROM) as PDTextField).value = userICS214.getFormattedOperationalPeriodStartDate()
            (ics214acroform.getField(ICS214PDFFieldNames.DATE_TO) as PDTextField).value = userICS214.getFormattedOperationalPeriodEndDate()
            (ics214acroform.getField(ICS214PDFFieldNames.TIME_FROM) as PDTextField).value = userICS214.getFormattedOperationalPeriodStartTime()
            (ics214acroform.getField(ICS214PDFFieldNames.TIME_TO) as PDTextField).value = userICS214.getFormattedOperationalPeriodEndTime()


            (ics214acroform.getField("8 Prepared by Name_2") as PDTextField).value = userICS214.preparedByName
            (ics214acroform.getField("PositionTitle_16") as PDTextField).value = userICS214.preparedByPositionTitle
            (ics214acroform.getField("DateTime_16") as PDTextField).value = userICS214.getFormattedPreparedByDateTime()

            for (i in 1 .. userCompleteICS214.resources.size) {
                if(i > 8)
                    break // only have 8 slots
                if(i < 7) //they change numbering scheme half way through for some reason
                    (ics214acroform.getField("NameRow$i"+"_3") as PDTextField).value = userCompleteICS214.resources[i-1].name
                else
                    (ics214acroform.getField("NameRow$i") as PDTextField).value = userCompleteICS214.resources[i-1].name
                (ics214acroform.getField("ICS PositionRow$i") as PDTextField).value = userCompleteICS214.resources[i-1].icsPosition
                (ics214acroform.getField("Home Agency and UnitRow$i") as PDTextField).value = userCompleteICS214.resources[i-1].homeAgency
            }

            val activityLog = userCompleteICS214.getSortedActivityLog()
            for (i in 1 .. activityLog.size) {
                if(i <= 24) {
                    (ics214acroform.getField("DateTimeRow$i") as PDTextField).value = activityLog[i-1].getFormattedEntryTime()
                    (ics214acroform.getField("Notable ActivitiesRow$i") as PDTextField).value = activityLog[i-1].activityDetails
                } else if (i in 25..48) {
                    (ics214acroform.getField("DateTimeRow"+(i-24)+"_2") as PDTextField).value = activityLog[i-1].getFormattedEntryTime()
                    (ics214acroform.getField("Notable ActivitiesRow"+(i-24)+"_2") as PDTextField).value = activityLog[i-1].activityDetails
                } else if (i in 49..60){
                    (ics214acroform.getField("DateTimeRow"+(i-24)) as PDTextField).value = activityLog[i-1].getFormattedEntryTime()
                    (ics214acroform.getField("Notable ActivitiesRow"+(i-24)) as PDTextField).value = activityLog[i-1].activityDetails
                } else
                    break //only support 60 fields right now. May convert pdf in the future
            }

            try {
                val signature = PDImageXObject.createFromFile(signatureFile.absolutePath, ics214PDf)
                //sign page 1
                val contents = PDPageContentStream(ics214PDf, ics214PDf.getPage(0), PDPageContentStream.AppendMode.APPEND, true)
                contents.drawImage(signature, 470F, 73F, 50F, 20F)
                contents.close()

                //sign page 2
                val contents2 = PDPageContentStream(ics214PDf, ics214PDf.getPage(1), PDPageContentStream.AppendMode.APPEND, true)
                contents2.drawImage(signature, 470F, 73F, 50F, 20F)
                contents2.close()
            } catch (e: Exception) {
                //we do nothing. If there's no signature, there's no signature
            }

            ics214PDf.save(fileOutputStream)
            fileOutputStream.close()
            ics214PDf.close()
            onSuccess()
            Timber.d("ics214 saved")
        }
    }
}