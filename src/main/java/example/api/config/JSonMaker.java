package example.api.config;

public class JSonMaker implements JSonManager{
    private StringBuilder json;
    private boolean isNew;

    public JSonMaker() {
        this.json = new StringBuilder();
        this.json.append("{");
    }

    @Override
    public JSonManager addLine(String key, String value) {

        if (json.length() > 1) {
            json.append(", ");
        }
        if (isNew) {
            json.setLength(0);
            json.append("{");
            isNew = false;
        }
        json.append("\"").append(key).append("\" : \"").append(value).append("\"");
        return this;
    }

    @Override
    public String build() {
        json.append("}");
        isNew = true;
        return json.toString();
    }
}
