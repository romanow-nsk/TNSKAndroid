package romanow.abc.tnsk.android;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import romanow.abc.tnsk.android.service.AppData;

public class SendMail extends AsyncTask{
    private MainActivity context;
    private Session session;
    private FileDescription data;
    private ProgressDialog progressDialog;
    public SendMail(final MainActivity context, FileDescription fd){
        try {
            this.context = context;
            data = fd;
            final LoginSettings set = AppData.ctx().loginSettings();
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", set.getMailHost());
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.socketFactory.port", ""+set.getMailPort()); //143
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.starttls.enable", "true");
            session = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(set.getMailBox(), set.getMailPass());
                   }
                });
            }
        catch (Exception ee){
            context.errorMes("Ошибка mail: "+ee.toString());
            }
        }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
        }
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
        }
    @Override
    protected Void doInBackground(Object[] objects) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(AppData.ctx().loginSettings().getMailBox()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(AppData.ctx().loginSettings().getMailToSend()));
            message.setSubject("Датчик "+data.toString()); // subject line
            String text = "Опоры России гудят "+data.toString()+" "+data.getGps().toString();
            MimeMultipart multipart = new MimeMultipart();
            //Первый кусочек - текст письма
            MimeBodyPart part1 = new MimeBodyPart();
            part1.addHeader("Content-Type", "text/plain; charset=UTF-8");
            part1.setDataHandler(new DataHandler(text, "text/plain; charset=\"utf-8\""));
            multipart.addBodyPart(part1);
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String fileName = AppData.ctx().androidFileDirectory()+"/"+data.getOriginalFileName();
            FileDataSource source = new FileDataSource(new File(fileName));
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(MimeUtility.encodeWord(fileName));
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            message.setText(text);
            message.setSentDate(new java.util.Date());
            Transport.send(message);
            } catch (Exception ee) {
                context.errorMes("Ошибка mail: "+ee.toString());
                }
        return null;
    }
}
