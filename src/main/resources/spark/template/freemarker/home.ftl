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
          <!---gotta make difficulty options-->
          <form id="diff_form" action="/game" method="GET">
              <div class="form-group">
                  <input type="radio" id="1" name="diff" value="1"> Standard
                  <input type="radio" id="2" name="diff" value="2"> Moderate
                  <input type="radio" id="3" name="diff" value="3"> Difficult
              </div>
              <p>
              <div class="form-group">
                  <button type="submit">Want to play a game?!?</button>
              </div>
              </p>
          </form>

        </p>
      <#else>
        <#if youWon>
          <p>
            Congratulations!  You must have read my mind.
            <br/><br/>
            <form id="diff_form" action="/game" method="GET">
              <div class="form-group">
                    <input type="radio" id="1" name="diff" value="1"> Standard
                    <input type="radio" id="2" name="diff" value="2"> Moderate
                    <input type="radio" id="3" name="diff" value="3"> Difficult
                </div>
                <p>
                <div class="form-group">
                    <button type="submit">Do it again</button>
                </div>
                </p>
            </form>
          </p>
        <#else>
          <p>
            Aww, too bad.  Better luck next time.
            <br/><br/>
            <form id="diff_form" action="/game" method="GET">
             <div class="form-group">
                <input type="radio" id="1" name="diff" value="1"> Standard
                <input type="radio" id="2" name="diff" value="2"> Moderate
                <input type="radio" id="3" name="diff" value="3"> Difficult
            </div>
            <p>
            <div class="form-group">
                <button type="submit">How about it?</button>
            </div>
            </p>
            </form>
          </p>
        </#if>
      </#if>
    
    </div>
  </div>
</body>
</html>
