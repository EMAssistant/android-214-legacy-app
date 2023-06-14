package com.randomvoids.emassistant.util.icsforms.ics213

import com.randomvoids.emassistant.model.Incident
import com.randomvoids.emassistant.util.icsforms.ics213.ICS213Details
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class ICS213Exporter {
    companion object {
        @Suppress("BlockingMethodInNonBlockingContext")
        fun saveICS213ToPDF(onSuccess: () -> Unit, onError: (String) -> Unit, incident: Incident, userICS213: ICS213Details, signatureFile: File, ics213InputStream: InputStream, fileOutputStream: OutputStream) {
            val ics213PDf = PDDocument.load(ics213InputStream)
            val ics213acroform = ics213PDf.documentCatalog.acroForm
            (ics213acroform.getField("1 Incident Name Optional") as PDTextField).value = incident.incidentName
            (ics213acroform.getField("2 To Name and Position") as PDTextField).value = userICS213.messageTo
            (ics213acroform.getField("3 From Name and Position") as PDTextField).value = userICS213.messageFrom
            (ics213acroform.getField("4 Subject") as PDTextField).value = userICS213.subject
            (ics213acroform.getField("5 Date") as PDTextField).value = userICS213.getFormattedMessageDate()
            (ics213acroform.getField("6 Time") as PDTextField).value = userICS213.getFormattedMessageTime()
            (ics213acroform.getField("7 Message") as PDTextField).value = userICS213.message
            (ics213acroform.getField("8 Approved by Name") as PDTextField).value = userICS213.approvedBy

            (ics213acroform.getField("PositionTitle_13") as PDTextField).value = userICS213.approvedByPositionTitle

            (ics213acroform.getField("9 Reply") as PDTextField).value = userICS213.reply
            (ics213acroform.getField("10 Replied by Name") as PDTextField).value = userICS213.repliedBy
            (ics213acroform.getField("PositionTitle_14") as PDTextField).value = userICS213.repliedByPositionTitle
            (ics213acroform.getField("DateTime_14") as PDTextField).value = userICS213.getFormattedReplyTimestamp()


            try {
                //TODO signature (Signature_19)
                val approvedBysignature = PDImageXObject.createFromFile(signatureFile.absolutePath, ics213PDf)
                //sign page 1
                val contents = PDPageContentStream(ics213PDf, ics213PDf.getPage(0), true, true)
                contents.drawImage(approvedBysignature, 470F, 73F, 50F, 20F)
                contents.close()

                //TODO signature (Signature_20)
                val replySignature = PDImageXObject.createFromFile(signatureFile.absolutePath, ics213PDf)
                contents.drawImage(replySignature, 470F, 73F, 50F, 20F)
                contents.close()

            } catch (e: Exception) {
                //we do nothing. If there's no signature, there's no signature
            }

            ics213PDf.save(fileOutputStream)
            fileOutputStream.close()
            ics213PDf.close()
            onSuccess()
            Timber.d("ics213 saved")
        }
    }
}