<div id="main">
<h1>Login into Atavism</h1>
<div id="login">
<h2>Login Form</h2>
<form action="" method="post">
<label>UserName :</label>
<input id="name" name="username" placeholder="username" type="text"><br>
<label>Password :  </label>
<input id="password" name="password" placeholder="**********" type="password"><br>
<input name="submit" type="submit" value=" Login "><br>
<span><?php if(isset($error))echo $error; ?></span>
</form>
</div>
</div>