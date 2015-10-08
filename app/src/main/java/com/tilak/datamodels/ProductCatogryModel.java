package com.tilak.datamodels;

public class ProductCatogryModel
{

	 private  String ProductCatagoryName="";
     private  String Image="";
     private  String Url="";
      
     /*********** Set Methods ******************/
      
     public void setProductCatagoryName(String CompanyName)
     {
         this.ProductCatagoryName = CompanyName;
     }
      
     public void setImage(String Image)
     {
         this.Image = Image;
     }
      
     public void setUrl(String Url)
     {
         this.Url = Url;
     }
      
     /*********** Get Methods ****************/
      
     public String getProductCatagoryName()
     {
         return this.ProductCatagoryName;
     }
      
     public String getImage()
     {
         return this.Image;
     }
  
     public String getUrl()
     {
         return this.Url;
     }  
}
