<h1 style="display: inline-flex; align-items: top; font-size: 32px;">
  <img src="screenshots/logo.png" alt="Logo" width="50px" style="margin-right: 15px; vertical-align: top;" />
  Watchly
</h1>

Watchly is an Android app designed to help users discover, organize, and store a personalized list of movies and TV shows. The app integrates with the **TMDb API** to fetch up-to-date movie data, while storing user preferences (watched, to-watch lists) in **Firebase Firestore**. Additionally, the app provides user authentication through both Google and email, an intuitive swipe interface and advanced search filters for better discovery experience.

---

## 🌟 Features

### 🔐 Authentication

- **Email & Password Authentication**: Users can easily create an account or log in using their email and password.
- **Google Login Integration**: Users can sign in with their Google account for faster authentication.

| ![](screenshots/welcome.jpg) | ![](screenshots/login.jpg) | ![](screenshots/register.jpg) | ![](screenshots/google_account.jpg) |
| ---------------------------- | -------------------------- | ----------------------------- | ----------------------------------- |

### 🎞️ Discover

The **Discover Screen** displays a dynamic set of **random popular movies** fetched from the TMDb API. This screen allows users to:

- **Swipe through movie cards**: Each card shows basic movie information such as the title, poster, and average rating.
  - 👈 **Skip**: Swipe left to skip the movie.
  - 👉 **Add to Watchlist**: Swipe right to add the movie to your **Watchlist**.
  - ✅ **Mark as Watched**: Click button to mark the movie as **Seen**.
  - ⬅️ **Return to Previous Movie**: Click button to return to the previously shown movie.
- The swipe interface makes it easy to navigate through a large collection of movies without feeling overwhelmed.

<table align="center">
  <tr>
    <td align="center" valign="top">
      <strong>📸 Screenshot: Main Screen</strong><br><br>
      <img src="screenshots/main.jpg" alt="Main Screen" width="150">
    </td>
    <td align="center">
      <strong>➡️ Browsing Movies – Using Buttons</strong><br><br>
      <img src="screenshots/discover_click.gif" alt="Discover Click Preview" width="200">
    </td>
    <td align="center">
      <strong>👆 Browsing Movies – Swiping Cards</strong><br><br>
      <img src="screenshots/discover_swipe.gif" alt="Discover Swipe Preview" width="200">
    </td>
  </tr>
</table>

### ✅ Seen

The **Seen Screen** displays movies that the user has already watched. This screen helps you keep track of your viewing history. Features include:

- **Detailed view**: Tap on any movie to see more information, including the plot and reviews.
- **Move to Watchlist**: If you change your mind and decide to watch a movie again later, you can move it back to the **Watchlist**.
- **Remove from List**: Users can remove a movie from the **Seen** list if they no longer want to keep it.

<table align="center">
  <tr>
    <td align="center" valign="top">
      <strong>📸 Screenshot: Seen Screen</strong><br><br>
      <img src="screenshots/seen_sc.jpg" alt="Seen Screen" width="200">
    </td>
    <td align="center" valign="top">
      <strong>Browsing Watched Movies</strong><br><br>
      <img src="screenshots/seen.gif" alt="Seen Preview" width="250">
    </td>
  </tr>
</table>

### 🕒 Watchlist

The **Watchlist Screen** is where you store movies that you plan to watch later. This screen is essentially your personal movie queue. Key features include:

- **Movie Details**: Each movie in the list shows basic information, including a poster, genre and average rating.
- **Move to Seen**: Once you've watched a movie, you can easily move it from the **Watchlist** to the **Seen** list.
- **Remove from List**: Users can remove movies from their Watchlist if they change their mind about watching them.

<table align="center">
  <tr>
    <td align="center" valign="top">
      <strong>📸 Screenshot: Watchlist Screen</strong><br><br>
      <img src="screenshots/watchlist_sc.jpg" alt="Watchlist Screen" width="200">
    </td>
    <td align="center" valign="top">
      <strong>Browsing Movies To Watch</strong><br><br>
      <img src="screenshots/watchlist.gif" alt="Watchlist Preview" width="250">
    </td>
  </tr>
</table>

### 🔎 Search

The **Search Screen** allows users to search for movies or TV shows. Features include:

- **Search by Title**: Find movies or TV shows based on their title.
- **Filter by**:
  - **Type**: Choose between _All_, _Movie_ or _TV Show_.
  - **Genre**: Select specific genres (e.g., Action, Drama, Comedy).
  - **Language**: Filter by language to find content in the desired language.
- **Sort by**:
  - **Popularity**: Sort movies by how popular they are.
  - **Release Date**: Sort by release date to find the newest movies.
  - **Rating**: Sort by movie rating to find the highest-rated content.
- **Add to Watchlist or Seen**: You can easily add search results to either the **Watchlist** or **Seen** lists.

<table align="center">
  <tr>
    <td align="center" valign="top">
      <strong>Advanced filtering</strong><br><br>
      <img src="screenshots/search_filters.gif" alt="Search Filters Preview" width="200">
    </td>
    <td align="center" valign="top">
      <strong>Searching by title</strong><br><br>
      <img src="screenshots/search_title.gif" alt="Search Title Preview" width="200">
    </td>
    <td align="center" valign="top">
      <strong>📸 Screenshot: Details</strong><br><br>
      <img src="screenshots/details.jpg" alt="Details" width="200">
    </td>
  </tr>
</table>

---

## 🛠️ Built With

The following technologies were used to build the app:

- **Java** – the programming language.
- **Android Studio** – the development environment used to build the app.
- **TMDb API** – a rich source of movie and TV show data, including details like plot summaries, ratings and posters.
- **Firebase Firestore** – cloud database service to store and sync user data, such as the **Watchlist** and **Seen** lists.
- **Firebase Authentication** – provides authentication mechanisms for logging in with either email or Google.
- **Glide** – image loading library for efficiently loading and displaying movie posters.
- **Retrofit** – library used to make network requests to the TMDb API.
- **Swipe Cards Library** – "com.lorentzos.swipecards:library" is used for implementing the swipeable card UI.

---
