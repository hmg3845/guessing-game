<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>${title}</title>
    <link rel="stylesheet" type="text/css" href="/styles/main.css">
</head>
<body>
    <h1>${title}</h1>
    
    <div class="body">
    
      <h2>Application Stats</h2>
      <p>
        ${gameStatsMessage}
        <br/><br/>
        ${playerStatsMessage}
      </p>
      
      <#if newPlayer>
        <p>
          <!---<a href="/game">Want to play a game?!?</a>  gotta make difficulty options-->
          <form id="diff_form" action="/game" method="GET">
          <input type="radio" id="1" name="diff" value="1"> Standard
          <input type="radio" id="2" name="diff" value="2"> Moderate
          <input type="radio" id="3" name="diff" value="3"> Difficult
          <button type="submit">Want to play a game?!?</button>
          </form>

        </p>
      <#else>
        <#if youWon>
          <p>
            Congratulations!  You must have read my mind.
            <br/><br/>
            <a href="/game">Do it again</a>
          </p>
        <#else>
          <p>
            Aww, too bad.  Better luck next time.
            <br/><br/>
            <!---<a href="/game">How about it?</a>-->
            <form id="diff_form" action="/game" method="GET">
              <input type="radio" id="1" name="diff" value="1"> Standard
              <input type="radio" id="2" name="diff" value="2"> Moderate
              <input type="radio" id="3" name="diff" value="3"> Difficult
              <button type="submit">Want to play a game?!?</button>
            </form>
          </p>
        </#if>
      </#if>
    
    </div>
  </div>
</body>
</html>
