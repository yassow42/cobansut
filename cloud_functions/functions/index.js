const functions = require('firebase-functions');

const admin = require('firebase-admin');

admin.initializeApp();

exports.takipIstegiBildirimiGonder = functions.database.ref("/Siparisler/{siparisler}").onCreate((data,context)=>{
	
	const siparis_key = context.params.siparisler;
	
	console.log("Siparisler: ", siparis_key);
	
/*	
	const siparis_veren_kisi = admin.database().ref('/Siparisler/{siparis_key}/siparis_veren').once('value');
	
	return siparis_veren_kisi.then(result =>{
		
		const siparis_veren_adi = result.val();
		console.log("SiparisVeren2", siparis_veren_adi);
	});
*/
	const bildirim = {
		notification: {
			
			title: "Sipariş",
			body: "Yeni Sipariş Geldi :)",
			icon: "default"
			
			
		},
	};
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };
	
	return admin.messaging().sendToTopic("msgNotification",bildirim,options)
	
	
});
	
	