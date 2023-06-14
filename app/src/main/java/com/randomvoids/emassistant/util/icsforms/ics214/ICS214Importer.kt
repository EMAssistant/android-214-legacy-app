package com.randomvoids.emassistant.util.icsforms.ics214

import com.randomvoids.emassistant.model.ICS214Details
import com.randomvoids.emassistant.model.ICS214WithActivityLogAndResources
import com.randomvoids.emassistant.model.ICSResource
import com.randomvoids.emassistant.model.Incident
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ICS214Importer {
    companion object {
        @Suppress("BlockingMethodInNonBlockingContext")
        fun importICS214FromPDF(
            onSuccess: () -> Unit,
            onError: (String) -> Unit,
            signatureFile: File,
            ics214InputStream: InputStream,
        ) {
            val ics214PDf = PDDocument.load(ics214InputStream)
            val ics214acroform = ics214PDf.documentCatalog.acroForm
            val dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm")
            val preparedByDateTime = Date.from(LocalDate.parse((ics214acroform.getField(ICS214PDFFieldNames.PREPARED_BY_DATE_TIME) as PDTextField).value, dateTimeFormatter).atStartOfDay(ZoneId.systemDefault()).toInstant())
            val opPeriodStartTimeString = (ics214acroform.getField(ICS214PDFFieldNames.DATE_FROM) as PDTextField).value + " " + (ics214acroform.getField(ICS214PDFFieldNames.TIME_FROM) as PDTextField).value
            val opPeriodStart = Date.from(LocalDate.parse(opPeriodStartTimeString, dateTimeFormatter).atStartOfDay(ZoneId.systemDefault()).toInstant())
            val opPeriodEndTimeString = (ics214acroform.getField(ICS214PDFFieldNames.DATE_TO) as PDTextField).value + " " + (ics214acroform.getField(ICS214PDFFieldNames.TIME_TO) as PDTextField).value
            val opPeriodEnd = Date.from(LocalDate.parse(opPeriodEndTimeString, dateTimeFormatter).atStartOfDay(ZoneId.systemDefault()).toInstant())

            //TODO incident id and name (if the name doesn't exist, create a new incident)
            val ics214 = ICS214Details(
                id = 1,
                incidentId = 1,
                teamName = (ics214acroform.getField(ICS214PDFFieldNames.TEAM_NAME) as PDTextField).value,
                teamIcsPosition = (ics214acroform.getField(ICS214PDFFieldNames.TEAM_ICS_POSITION) as PDTextField).value,
                homeAgency = (ics214acroform.getField(ICS214PDFFieldNames.HOME_AGENCY) as PDTextField).value,
                preparedByName = (ics214acroform.getField(ICS214PDFFieldNames.PREPARED_BY_NAME) as PDTextField).value,
                preparedByPositionTitle = (ics214acroform.getField(ICS214PDFFieldNames.PREPARED_BY_POSITION_TITLE) as PDTextField).value,
                preparedByDateTime = preparedByDateTime,
                operationalPeriodStartTime = opPeriodStart,
                operationalPeriodEndTime = opPeriodEnd,
                isSubmitted = false
            )
            var resources: MutableList<ICSResource> = ArrayList()
            for(i in 1 .. 8) {
                var resourceName = ""
                resourceName = if(i < 7) (ics214acroform.getField("NameRow$i"+"_3") as PDTextField).value else (ics214acroform.getField("NameRow$i") as PDTextField).value
                if(resourceName.isNotEmpty()) {
                    resources.add(ICSResource(ics214Id = 1, //TODO
                            name = resourceName,
                            icsPosition = (ics214acroform.getField("ICS PositionRow$i") as PDTextField).value,
                            homeAgency = (ics214acroform.getField("Home Agency and UnitRow$i") as PDTextField).value
                    ))
                }
            }
        }
    }
}