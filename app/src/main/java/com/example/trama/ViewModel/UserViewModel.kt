package com.example.trama.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trama.Data.ApiService
import com.example.trama.Data.Model.Movie
import com.example.trama.Data.Model.Notification
import com.example.trama.Data.Model.Review
import com.example.trama.Data.Model.User
import com.example.trama.State.UserState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch


//perfi usuario actual:
//favoritos y pelis vistas, reseñas nuestras y de otros, seguimiento y seguidos
class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseDatabase.getInstance()
    private val api  = ApiService.getInstance()
    private val usuariosRef  get() = db.getReference("Usuarios")
    private val reviewsRef   get() = db.getReference("Reseñas")
    private var watchedRef:     DatabaseReference? = null
    private var favoritesRef:   DatabaseReference? = null
    private var userRef:        DatabaseReference? = null

    var state by mutableStateOf(UserState())
        private set
    var currentUserProfile   by mutableStateOf<User?>(null)       ; private set
    var userReviews          by mutableStateOf<List<Review>>(emptyList()) ; private set
    var watchedMoviesIds     by mutableStateOf<List<Int>>(emptyList())    ; private set
    var watchedMoviesDetails by mutableStateOf<List<Movie>>(emptyList())  ; private set
    var favoritesIds         by mutableStateOf<List<Int>>(emptyList())    ; private set
    var favoritesDetails     by mutableStateOf<List<Movie>>(emptyList())  ; private set
    var viewedUserProfile    by mutableStateOf<User?>(null)        ; private set
    var viewedUserReviews    by mutableStateOf<List<Review>>(emptyList()) ; private set
    var viewedUserFavorites  by mutableStateOf<List<Movie>>(emptyList())  ; private set
    var viewedUserWatched    by mutableStateOf<List<Movie>>(emptyList())  ; private set
    var followState by mutableStateOf(FollowState.NONE) ; private set
    enum class FollowState { NONE, PENDING, FOLLOWING }
    private var userListener:        ValueEventListener? = null
    private var watchedListener:     ValueEventListener? = null
    private var favoritesListener:   ValueEventListener? = null
    private var userReviewsListener: ValueEventListener? = null


    //inicializa la sesion del usuario
    fun initUserSession(authenticatedUid: String?) {
        removeListeners()

        if (!authenticatedUid.isNullOrBlank()) {
            userRef      = usuariosRef.child(authenticatedUid)
            watchedRef   = db.getReference("Vistas").child(authenticatedUid)
            favoritesRef = db.getReference("Favoritos").child(authenticatedUid)

            observeUser()
            observeWatchedMovies()
            observeFavorites()
            observeUserReviews(authenticatedUid)
            loadFollowingFeed()
        } else {
            Log.e("DEBUG_TRAMA", "initUserSession fue llamado pero el UID es nulo o vacío.")
        }
    }

    //escucha los cambios en el perfil
    private fun observeUser() {
        userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUserProfile = snapshot.getValue(User::class.java)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        userRef?.addValueEventListener(userListener!!)
    }

    //edita usuario
    fun saveUserProfile(username: String, biography: String, profilePicture: String) {
        val uid = auth.currentUser?.uid ?: return
        usuariosRef.child(uid).updateChildren(
            mapOf(
                "username"       to username,
                "biography"      to biography,
                "profilePicture" to profilePicture
            )
        )
    }

    //escucha las pelis vistas y las carga de la api
    private fun observeWatchedMovies() {
        watchedListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull { it.getValue(Long::class.java)?.toInt() }
                watchedMoviesIds = ids
                loadWatchedMoviesDetails(ids)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        watchedRef?.addValueEventListener(watchedListener!!)
    }

    private fun loadWatchedMoviesDetails(ids: List<Int>) {
        viewModelScope.launch {
            watchedMoviesDetails = try { ids.map { api.getMovieDetails(it) } }
            catch (e: Exception) { emptyList() }
        }
    }

    //añade o eliminar peli vista
    fun toggleWatchedMovie(movieId: Int) {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.getReference("Vistas").child(uid).child(movieId.toString())
        if (watchedMoviesIds.contains(movieId)) {
            watchedMoviesIds = watchedMoviesIds - movieId; ref.removeValue()
        } else {
            watchedMoviesIds = watchedMoviesIds + movieId; ref.setValue(movieId)
        }
    }

    //escucha las pelis favs y las carga de la api
    private fun observeFavorites() {
        favoritesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull { it.getValue(Long::class.java)?.toInt() }
                favoritesIds = ids
                loadFavoritesDetails(ids)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        favoritesRef?.addValueEventListener(favoritesListener!!)
    }

    private fun loadFavoritesDetails(ids: List<Int>) {
        viewModelScope.launch {
            favoritesDetails = try { ids.map { api.getMovieDetails(it) } }
            catch (e: Exception) { emptyList() }
        }
    }

    //añade o quita peli fav
    fun toggleFavorite(movieId: Int) {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.getReference("Favoritos").child(uid).child(movieId.toString())
        if (favoritesIds.contains(movieId)) {
            favoritesIds = favoritesIds - movieId; ref.removeValue()
        } else {
            favoritesIds = favoritesIds + movieId; ref.setValue(movieId)
        }
    }

    //escucha en tiempo real las reseñas del usuario actual
    private fun observeUserReviews(uid: String) {
        userReviewsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userReviews = snapshot.children
                    .mapNotNull { it.getValue(Review::class.java) }
                    .sortedByDescending { it.timestamp }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        reviewsRef.orderByChild("userId").equalTo(uid)
            .addValueEventListener(userReviewsListener!!)
    }

    //publica una review
    fun publishReview(movieId: Int, movieTitle: String, rating: Float, comment: String) {
        val uid = auth.currentUser?.uid ?: return
        usuariosRef.child(uid).get().addOnSuccessListener { snapshot ->
            val usernameFinal = snapshot.child("username").getValue(String::class.java)
                ?.takeIf { it.isNotBlank() }
                ?: "user_${uid.take(4).lowercase()}"

            val reviewRef = reviewsRef.push()
            reviewRef.setValue(
                Review(
                    id         = reviewRef.key.orEmpty(),
                    movieId    = movieId,
                    movieTitle = movieTitle,
                    userId     = uid,
                    username   = usernameFinal,
                    rating     = rating,
                    comment    = comment.trim(),
                    timestamp  = System.currentTimeMillis()
                )
            )
        }
    }

    //edita reseña
    fun editReview(reviewId: String, newRating: Float, newComment: String) {
        reviewsRef.child(reviewId).updateChildren(
            mapOf("rating" to newRating, "comment" to newComment.trim(), "timestamp" to System.currentTimeMillis())
        )
    }

    fun deleteReview(reviewId: String) { reviewsRef.child(reviewId).removeValue() }

    //carga perfil, reseñas, vistas y favs de otro usuario
    fun loadExternalUserData(userId: String) {
        viewedUserProfile   = null
        viewedUserReviews   = emptyList()
        viewedUserFavorites = emptyList()
        viewedUserWatched   = emptyList()

        usuariosRef.child(userId).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                viewedUserProfile = user
            }
            .addOnFailureListener { viewedUserProfile = null }

        reviewsRef.get().addOnSuccessListener { snapshot ->
            val reviewsList = snapshot.children.mapNotNull { it.getValue(Review::class.java) }
                .filter { it.userId == userId }
                .sortedByDescending { it.timestamp }

            viewedUserReviews = reviewsList
        }.addOnFailureListener {
            viewedUserReviews = emptyList()
        }

        db.getReference("Favoritos").child(userId).get().addOnSuccessListener { snapshot ->
            val ids = snapshot.children.mapNotNull { it.getValue(Long::class.java)?.toInt() }
            viewModelScope.launch {
                viewedUserFavorites = try { ids.map { api.getMovieDetails(it) } } catch (e: Exception) { emptyList() }
            }
        }

        db.getReference("Vistas").child(userId).get().addOnSuccessListener { snapshot ->
            val ids = snapshot.children.mapNotNull { it.getValue(Long::class.java)?.toInt() }
            viewModelScope.launch {
                viewedUserWatched = try { ids.map { api.getMovieDetails(it) } } catch (e: Exception) { emptyList() }
            }
        }
    }

    //comprueba solicitudes y si seguimos a alguien o no
    fun checkFollowState(targetUid: String) {
        val myUid = auth.currentUser?.uid ?: return
        if (targetUid == myUid) {
            followState = FollowState.NONE
            return
        }
        usuariosRef.child(targetUid).get().addOnSuccessListener { snapshot ->
            followState = when {
                snapshot.child("followers").hasChild(myUid)       -> FollowState.FOLLOWING
                snapshot.child("pendingRequests").hasChild(myUid) -> FollowState.PENDING
                else                                              -> FollowState.NONE
            }
        }
    }

    //manda solicitud y crea notificacion
    fun sendFollowRequest(targetUid: String) {
        val myUid = auth.currentUser?.uid ?: return
        if (targetUid == myUid) return

        usuariosRef.child(targetUid).child("pendingRequests").child(myUid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                followState = FollowState.PENDING
                return@addOnSuccessListener
            }

            usuariosRef.child(targetUid).child("pendingRequests").child(myUid).setValue(true)
                .addOnSuccessListener {
                    followState = FollowState.PENDING

                    val notifRef = db.getReference("Notifications").child(targetUid).push()
                    val notification = Notification(
                        id = notifRef.key ?: "",
                        type = "follow_request",
                        fromUid = myUid,
                        toUid = targetUid,
                        message = "Te ha enviado una solicitud de seguimiento",
                        timestamp = System.currentTimeMillis(),
                        read = false
                    )
                    notifRef.setValue(notification)
                }
        }
    }

    //acepta solicidud de seguimiento y actualiza seguidores/seguidos
    fun acceptFollowRequest(requesterUid: String) {
        val myUid = auth.currentUser?.uid ?: return

        usuariosRef.child(myUid).child("pendingRequests").child(requesterUid).removeValue()
        usuariosRef.child(myUid).child("followers").child(requesterUid).setValue(true)
        usuariosRef.child(requesterUid).child("following").child(myUid).setValue(true)

        db.getReference("Notifications").child(myUid).get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { notifSnap ->
                val type = notifSnap.child("type").getValue(String::class.java)
                val fromUid = notifSnap.child("fromUid").getValue(String::class.java)
                if (type == "follow_request" && fromUid == requesterUid) {
                    notifSnap.ref.removeValue()
                }
            }
        }
    }

    //eliminar solicitud y notificacion
    fun rejectFollowRequest(requesterUid: String) {
        val myUid = auth.currentUser?.uid ?: return
        usuariosRef.child(myUid).child("pendingRequests").child(requesterUid).removeValue()

        db.getReference("Notifications").child(myUid).get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { notifSnap ->
                val type = notifSnap.child("type").getValue(String::class.java)
                val fromUid = notifSnap.child("fromUid").getValue(String::class.java)
                if (type == "follow_request" && fromUid == requesterUid) {
                    notifSnap.ref.removeValue()
                }
            }
        }
    }

    //deja de seguir
    fun unfollowUser(targetUid: String) {
        val myUid = auth.currentUser?.uid ?: return
        usuariosRef.child(targetUid).child("followers").child(myUid).removeValue()
        usuariosRef.child(myUid).child("following").child(targetUid).removeValue()
            .addOnSuccessListener { followState = FollowState.NONE }
    }

    //busca user por username
    fun searchUsers(query: String) {
        if (query.isBlank()) {
            state = state.copy(userSearchResults = emptyList()); return
        }
        state = state.copy(isSearchingUsers = true)
        val q = query.trim().removePrefix("@")

        usuariosRef.get().addOnSuccessListener { snapshot ->
            val myUid = auth.currentUser?.uid
            val filtrados = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                .filter { user ->
                    user.uid != myUid && user.username.lowercase().contains(q.lowercase())
                }

            state = state.copy(userSearchResults = filtrados, isSearchingUsers = false)
        }.addOnFailureListener {
            state = state.copy(isSearchingUsers = false)
        }
    }

    //carga reseñas de usuarios que seguimos
    fun loadFollowingFeed() {
        val myUid = auth.currentUser?.uid ?: return
        state = state.copy(isLoadingFeed = true)

        usuariosRef.child(myUid).child("following").get()
            .addOnSuccessListener { followingSnapshot ->
                val followingUids = followingSnapshot.children.mapNotNull { it.key }
                    .filter { it != myUid && it.length > 5 }

                if (followingUids.isEmpty()) {
                    state = state.copy(followingFeed = emptyList(), isLoadingFeed = false)
                    return@addOnSuccessListener
                }

                reviewsRef.get().addOnSuccessListener { reviewsSnapshot ->
                    val todasLasResenas = reviewsSnapshot.children.mapNotNull { it.getValue(Review::class.java) }
                    val resenasFiltradas = todasLasResenas.filter { review ->
                        followingUids.contains(review.userId)
                    }.sortedByDescending { it.timestamp }

                    state = state.copy(followingFeed = resenasFiltradas, isLoadingFeed = false)
                }.addOnFailureListener {
                    state = state.copy(isLoadingFeed = false)
                }
            }
            .addOnFailureListener {
                state = state.copy(isLoadingFeed = false)
            }
    }

    //lista uid con solicitudes pendientes
    val pendingRequestUids: List<String>
        get() = currentUserProfile?.pendingRequests?.keys?.toList() ?: emptyList()

    fun clearUserData() {
        removeListeners()
        currentUserProfile = null
        userReviews = emptyList()
        watchedMoviesIds = emptyList()
        watchedMoviesDetails = emptyList()
        favoritesIds = emptyList()
        favoritesDetails = emptyList()
        viewedUserProfile = null
        viewedUserReviews = emptyList()
        viewedUserFavorites = emptyList()
        viewedUserWatched = emptyList()
        followState = FollowState.NONE
        state = UserState()
    }

    private fun removeListeners() {
        userListener?.let { userRef?.removeEventListener(it) }
        watchedListener?.let { watchedRef?.removeEventListener(it) }
        favoritesListener?.let { favoritesRef?.removeEventListener(it) }
        userReviewsListener?.let { reviewsRef.removeEventListener(it) }
    }

    override fun onCleared() {
        super.onCleared()
        removeListeners()
    }

    fun fetchUsernameForUid(uid: String, onResult: (String) -> Unit) {
        usuariosRef.child(uid).child("username").get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.getValue(String::class.java)
                onResult(if (name.isNullOrBlank()) uid.take(6) else name)
            }
    }
}