package com.creativeoffice.cobansut.utils

 class Datalar {

    public fun mahalle():ArrayList<String>{
        var mahalleler = ArrayList<String>()

        if (Utils.secilenBolge == "Burgaz"){
            mahalleler.add("Market")
            mahalleler.add("8 Kasım")
            mahalleler.add("Atatürk")
            mahalleler.add("Barış")
            mahalleler.add("Cumhuriyet")
            mahalleler.add("Dere")
            mahalleler.add("Durak")
            mahalleler.add("Fatih")
            mahalleler.add("Gençlik")
            mahalleler.add("Gündoğu")
            mahalleler.add("Güneş")
            mahalleler.add("Hürriyet")
            mahalleler.add("İnönü")
            mahalleler.add("İstiklal")
            mahalleler.add("Kocasinan")
            mahalleler.add("Kurtuluş")
            mahalleler.add("Özerler")
            mahalleler.add("Sevgi")
            mahalleler.add("Siteler")
            mahalleler.add("Yeni")
            mahalleler.add("Yıldırım")
            mahalleler.add("Yıldız")
            mahalleler.add("Yılmaz")
            mahalleler.add("Zafer")
        }
        else if (Utils.secilenBolge == "Corlu"){
            mahalleler.add("Market")
            mahalleler.add("Alipaşa")
            mahalleler.add("Cemaliye")
            mahalleler.add("Çoban Çeşme")
            mahalleler.add("Cumhuriyet")
            mahalleler.add("Dere")
            mahalleler.add("Esentepe")
            mahalleler.add("Hatip")
            mahalleler.add("Havuzlar")
            mahalleler.add("Hıdırağa")
            mahalleler.add("Hürriyet")
            mahalleler.add("İnönü")
            mahalleler.add("Kazımiye")
            mahalleler.add("Kemalettin")
            mahalleler.add("Muhittin")
            mahalleler.add("Nusratiye")
            mahalleler.add("Reşadiye")
            mahalleler.add("Rumeli")
            mahalleler.add("Şeyh Sinan")
            mahalleler.add("Silahtarağa")
            mahalleler.add("Zafer")
        }
        else if (Utils.secilenBolge =="Cerkez"){
            mahalleler.add("Market")
            mahalleler.add("Bağlık")
            mahalleler.add("Cumhuriyet")
            mahalleler.add("Fatih")
            mahalleler.add("Fevzi Paşa")
            mahalleler.add("Gazi Mustafa Kemal Paşa")
            mahalleler.add("Gazi Osman Paşa")
            mahalleler.add("İstasyon")
            mahalleler.add("Kızıl Pınar")
            mahalleler.add("VeliKöy")
            mahalleler.add("Yıldırım Beyazıt")
        }

        return mahalleler
    }


}