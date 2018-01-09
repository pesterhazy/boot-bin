package boot.bin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Arrays;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BootSnapshotVersionFetcher {

    private static class MavenMetadataHandler extends DefaultHandler {
        String snapshotVersion;
        Deque<String> stack = new ArrayDeque<>();

        String marker[] = {"value", "snapshotVersion", "snapshotVersions", "versioning", "metadata"};

        public String getSnapshotVersion() {
            return snapshotVersion; }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            stack.push(qName); }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            stack.pop(); }

        public void characters(char ch[], int start, int length) throws SAXException {
            if (Arrays.equals(stack.toArray(), marker) && snapshotVersion == null) {
                // String versionString = new String(ch, start, length);
                // System.out.println("version " + versionString);
                snapshotVersion = new String(ch, start, length); }}};

    public static String lastSnapshot(String version)  throws Exception {
        MavenMetadataHandler handler = new MavenMetadataHandler();
        try { SAXParserFactory factory = SAXParserFactory.newInstance();
              SAXParser saxParser = factory.newSAXParser();
              String metadataFile = String.format("https://clojars.org/repo/boot/base/%s/maven-metadata.xml", version);
              saxParser.parse(metadataFile, handler); }
        catch (Exception e) { e.printStackTrace(); }

        return handler.getSnapshotVersion(); }}