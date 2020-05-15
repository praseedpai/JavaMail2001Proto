//////////////////////////////
// 
// 
//
//
//
//
//
//
public class MailAddress
{
  public MailAddress()
  {
    super();
  }

  public static MailAddress parseAddress(String address)
  {
    MailAddress addr = new MailAddress();
    addr.setAddress(address);
    return addr;
  }
  public void setAddress(String address)
  {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  public void setRealName(String realName)
  {
    this.realName = realName;
  }

  public String getRealName() {
    return realName;
  }
  public String toString()
  {
    StringBuffer buff = new StringBuffer();

    // real life name is OPTIONAL
    if (realName != null) {
      buff.append("\"" + realName + "\" ");
    }

    // address is REQUIRED
    if (address != null) {
      buff.append("<" + address + ">");
    }
    else {
      return null;
    }

    return buff.toString();
  }

  private String address = null;
  private String realName = null;
}

