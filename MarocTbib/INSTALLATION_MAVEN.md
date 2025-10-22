# Installation de Maven pour démarrer le projet

## Option 1 : Installer Maven (RECOMMANDÉ)

### Téléchargement
1. Allez sur : https://maven.apache.org/download.cgi
2. Téléchargez : **apache-maven-3.9.6-bin.zip**
3. Extrayez le fichier dans : `C:\Program Files\Apache\maven`

### Configuration PATH
1. Appuyez sur `Windows + Pause` (ou clic droit sur "Ce PC" → Propriétés)
2. Cliquez sur "Paramètres système avancés"
3. Cliquez sur "Variables d'environnement"
4. Dans "Variables système", trouvez `Path` et cliquez sur "Modifier"
5. Cliquez sur "Nouveau" et ajoutez : `C:\Program Files\Apache\maven\bin`
6. Cliquez sur "OK" pour tout fermer

### Vérification
Ouvrez un **nouveau** terminal et tapez :
```bash
mvn --version
```

Vous devriez voir la version de Maven.

### Démarrer le projet
```bash
cd C:\Users\HP\Desktop\Projet_Complet\APP\MarocTbib\backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Option 2 : Utiliser l'IDE (Plus simple)

### Si vous avez IntelliJ IDEA ou Eclipse :
1. Ouvrez l'IDE
2. Importez le projet : `File → Open → Sélectionnez le dossier backend`
3. L'IDE détectera automatiquement le fichier `pom.xml`
4. Clic droit sur `MarocTbibApplication.java`
5. Sélectionnez "Run 'MarocTbibApplication'"

---

## Option 3 : Télécharger Maven Wrapper

Exécutez ces commandes dans le dossier backend :

```bash
cd C:\Users\HP\Desktop\Projet_Complet\APP\MarocTbib\backend

REM Créer le dossier .mvn/wrapper
mkdir .mvn\wrapper

REM Télécharger les fichiers nécessaires
curl -o .mvn\wrapper\maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
curl -o .mvn\wrapper\maven-wrapper.properties https://raw.githubusercontent.com/takari/maven-wrapper/master/maven-wrapper.properties
curl -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd
```

Puis démarrez avec :
```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Quelle option choisir ?

- **Option 1** : Si vous voulez utiliser Maven pour tous vos projets Java
- **Option 2** : Si vous avez déjà un IDE installé (plus rapide)
- **Option 3** : Si vous voulez juste ce projet sans installer Maven globalement
