// src/pages/Landing.jsx
import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Gift, Calendar, Heart, Moon, Sun, Zap, Users, CheckCircle, Mail, Smile, Link2, Star, Send, X, LogOut, Camera, Edit2, Check, User } from 'lucide-react';
import './landing.css';
import toast from 'react-hot-toast';
import { useNavigate } from "react-router-dom";
import { useAuth } from '../contexts/AuthContext';

const textVariants = {
  initial: {
    x: -500,
    opacity: 0,
  },
  animate: {
    x: 0,
    opacity: 1,
    transition: {
      duration: 1,
      staggerChildren: 0.1,
    },
  },
};

const sliderVariants = {
  initial: {
    x: '150%',
  },
  animate: {
    x: '-100%',
    transition: {
      ease: 'linear',
      repeat: Infinity,
      repeatType: 'loop',
      duration: 35,
    },
  },
};

const scrollButtonVariants = {
  animate: {
    y: [0, 10, 0],
    transition: {
      duration: 1.5,
      repeat: Infinity,
    },
  },
};

const Landing = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  // ============= PROFILE STATE =============
  const [profileData, setProfileData] = useState({
    profileImage: null,
    name: '',
    username: '',
    email: ''
  });
  const [editData, setEditData] = useState({
    profileImage: null,
    name: '',
    username: '',
    email: ''
  });
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [isFetchingProfile, setIsFetchingProfile] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // ============= LANDING PAGE STATE =============
  const [stats, setStats] = useState({
    events: 0,
    donations: 0,
    giftPools: 0,
  });

  const [currentIconIndex, setCurrentIconIndex] = useState(0);
  const [isDarkMode, setIsDarkMode] = useState(true);
  const [showFeedbackModal, setShowFeedbackModal] = useState(false);
  const [isSubmittingFeedback, setIsSubmittingFeedback] = useState(false);
  const [allUserFeedbacks, setAllUserFeedbacks] = useState([]);
  const [feedbackCount, setFeedbackCount] = useState(0);
  const [isLoadingFeedbacks, setIsLoadingFeedbacks] = useState(false);

  const [feedbackData, setFeedbackData] = useState({
    name: '',
    email: '',
    message: '',
    type: 'event',
    rating: 5,
  });

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: '',
  });
  const [formStatus, setFormStatus] = useState('');

  const icons = [
    {
      component: Gift,
      className: 'h-10 w-10 text-purple-400',
      label: 'Gift Pooling',
    },
    {
      component: Calendar,
      className: 'h-10 w-10 text-blue-400',
      label: 'Events',
    },
    {
      component: Heart,
      className: 'h-10 w-10 text-red-400',
      label: 'Donations',
    },
  ];

  // ============= PROFILE FUNCTIONS =============
  const fetchUserData = async () => {
    setIsFetchingProfile(true);
    try {
      let token = localStorage.getItem('token') || 
                 localStorage.getItem('jwt_token') || 
                 localStorage.getItem('auth_token') ||
                 localStorage.getItem('authToken');
      
      if (!token) {
        setIsFetchingProfile(false);
        return;
      }

      const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/getProfile`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        const userData = await response.json();
        setProfileData({
          profileImage: userData.profileImage || null,
          name: userData.name || '',
          username: userData.username || '',
          email: userData.email || ''
        });
        setEditData({
          profileImage: userData.profileImage || null,
          name: userData.name || '',
          username: userData.username || '',
          email: userData.email || ''
        });
      }
    } catch (error) {
      console.error('Error fetching profile:', error);
    } finally {
      setIsFetchingProfile(false);
    }
  };

  const handleProfileImageChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setEditData(prev => ({
          ...prev,
          profileImage: reader.result
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleEditInputChange = (e) => {
    const { name, value } = e.target;
    setEditData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSaveProfile = async () => {
    setIsLoading(true);
    try {
      let token = localStorage.getItem('token') || 
                 localStorage.getItem('jwt_token') || 
                 localStorage.getItem('auth_token') ||
                 localStorage.getItem('authToken');

      const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/updateProfile`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          name: editData.name,
          username: editData.username,
          email: editData.email,
          profileImage: editData.profileImage
        })
      });

      if (response.ok) {
        setProfileData(editData);
        setIsEditing(false);
        toast.success('Profile updated successfully!');
      } else {
        toast.error('Failed to update profile');
      }
    } catch (error) {
      console.error('Error updating profile:', error);
      toast.error('Error updating profile');
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // ============= LANDING PAGE FUNCTIONS =============
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIconIndex((prevIndex) => (prevIndex + 1) % icons.length);
    }, 2000);
    return () => clearInterval(interval);
  }, [icons.length]);

  const fetchApprovedFeedbacks = async () => {
    setIsLoadingFeedbacks(true);
    try {
      const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/feedback/approved`);
      if (response.ok) {
        const data = await response.json();
        const sortedFeedbacks = data.sort((a, b) => {
          return new Date(b.createdAt) - new Date(a.createdAt);
        });
        setAllUserFeedbacks(sortedFeedbacks);
        setFeedbackCount(data.length);
      }
    } catch (error) {
      console.error('Error fetching feedbacks:', error);
    } finally {
      setIsLoadingFeedbacks(false);
    }
  };

  const fetchStats = async () => {
    try {
      const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/stats`);
      if (response.ok) {
        const data = await response.json();
        setStats({
          events: data.events || 0,
          donations: data.donations || 0,
          giftPools: data.giftPools || 0,
        });
      }
    } catch (error) {
      console.error('Error fetching stats:', error);
    }
  };

  useEffect(() => {
    fetchStats();
    fetchApprovedFeedbacks();
    if (user) {
      fetchUserData();
    }
  }, [user]);

  useEffect(() => {
    const handleFeedbackSubmitted = () => {
      fetchApprovedFeedbacks();
    };
    window.addEventListener('feedbackSubmitted', handleFeedbackSubmitted);
    return () => {
      window.removeEventListener('feedbackSubmitted', handleFeedbackSubmitted);
    };
  }, []);

  useEffect(() => {
    const saved = localStorage.getItem('isDarkMode');
    if (saved !== null) {
      const isDark = JSON.parse(saved);
      setIsDarkMode(isDark);
      applyTheme(isDark);
    } else {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      setIsDarkMode(prefersDark);
      applyTheme(prefersDark);
    }
  }, []);

  const applyTheme = (isDark) => {
    if (isDark) {
      document.documentElement.classList.remove('light');
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
      document.documentElement.classList.add('light');
    }
  };

  const toggleDarkMode = () => {
    const newMode = !isDarkMode;
    setIsDarkMode(newMode);
    localStorage.setItem('isDarkMode', JSON.stringify(newMode));
    applyTheme(newMode);
  };

  const handleSignIn = () => {
    navigate("/login");
  };

  const handleCreateAccount = () => {
    navigate("/register");
  };

  const handleFeedbackChange = (e) => {
    const { name, value } = e.target;
    setFeedbackData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFeedbackRating = (rating) => {
    setFeedbackData((prev) => ({
      ...prev,
      rating,
    }));
  };

  const handleFeedbackSubmit = async (e) => {
    e.preventDefault();
    setIsSubmittingFeedback(true);

    try {
      const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/feedback`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(feedbackData),
      });

      const data = await response.json();

      if (response.ok) {
        toast.success('Thank you! Your feedback has been submitted! 🙏');
        setShowFeedbackModal(false);
        setFeedbackData({
          name: '',
          email: '',
          message: '',
          type: 'event',
          rating: 5,
        });
        await fetchApprovedFeedbacks();
      } else {
        toast.error(data.error || 'Failed to submit feedback');
      }
    } catch (error) {
      console.error('Error submitting feedback:', error);
      toast.error('Error submitting feedback');
    } finally {
      setIsSubmittingFeedback(false);
    }
  };

  const handleSkipFeedback = () => {
    setShowFeedbackModal(false);
    handleSignIn();
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem('jwt_token');

    if (!token) {
      toast.error('Please login to send a query');
      navigate('/login');
      return;
    }

    setFormStatus('sending');

    try {
      const response = await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/query`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        toast.success('Query sent successfully!');
        setFormStatus('success');
        setFormData({
          name: '',
          email: '',
          phone: '',
          subject: '',
          message: '',
        });
        setTimeout(() => setFormStatus(''), 3000);
      } else {
        const errorData = await response.json();
        toast.error(errorData.message || 'Failed to send query');
        setFormStatus('error');
        setTimeout(() => setFormStatus(''), 3000);
      }
    } catch (error) {
      console.error('Error submitting form:', error);
      toast.error('Error submitting query');
      setFormStatus('error');
      setTimeout(() => setFormStatus(''), 3000);
    }
  };

  const defaultReviews = [
    {
      id: 1,
      name: 'Arjun Sharma',
      role: 'Event Organizer',
      text: 'NexFund made fundraising incredibly easy and transparent.',
      type: 'event',
    },
    {
      id: 2,
      name: 'Priya Patel',
      role: 'Charity Worker',
      text: 'Best platform for pooling funds. Secure and reliable.',
      type: 'donation',
    },
    {
      id: 3,
      name: 'Rajesh Kumar',
      role: 'Entrepreneur',
      text: 'Collected startup funds in days. Highly recommended!',
      type: 'giftpool',
    },
  ];

  const displayedReviews = allUserFeedbacks.length > 0
    ? allUserFeedbacks.slice(0, 3)
    : defaultReviews;

  const features = [
    {
      id: 1,
      title: 'Zero Commission',
      description: 'Every rupee goes to creators. No Razorpay. No middlemen.',
      icon: Zap,
      color: 'blue',
    },
    {
      id: 2,
      title: 'Direct UPI Transfer',
      description: 'Direct to your UPI ID. Full transparency, zero delays.',
      icon: CheckCircle,
      color: 'blue',
    },
    {
      id: 3,
      title: 'Real-Time Dashboard',
      description: 'Track events, donations, gift pools, and funding amounts instantly.',
      icon: Calendar,
      color: 'red',
    },
    {
      id: 4,
      title: 'Participant Tracking',
      description: 'See who participated, donated, or contributed in real-time.',
      icon: Users,
      color: 'purple',
    },
    {
      id: 5,
      title: 'Auto Verification',
      description: 'Verification emails sent automatically to all participants.',
      icon: Mail,
      color: 'blue',
    },
    {
      id: 6,
      title: 'User Friendly',
      description: 'Simple, intuitive interface that anyone can use effortlessly.',
      icon: Smile,
      color: 'purple',
    },
  ];

  return (
    <div className="landing-page">
      <nav className="navbar">
        <div className="navbar-container">
          <div className="navbar-logo">
            <div className="logo-icon">
              {icons[currentIconIndex].component &&
                React.createElement(icons[currentIconIndex].component, { className: icons[currentIconIndex].className })}
            </div>
            <div className="logo-text">
              <span className="logo-title">NexFund</span>
              <span className="logo-subtitle">{icons[currentIconIndex].label}</span>
            </div>
          </div>
          <div className="navbar-links">
            {/* DARK MODE TOGGLE */}
            <button
              onClick={toggleDarkMode}
              className="theme-toggle-btn"
              title={isDarkMode ? 'Switch to light mode' : 'Switch to dark mode'}
            >
              <div className="toggle-icon">
                <Sun
                  className={`sun-icon ${isDarkMode ? 'hidden' : 'visible'}`}
                  size={20}
                />
                <Moon
                  className={`moon-icon ${isDarkMode ? 'visible' : 'hidden'}`}
                  size={20}
                />
              </div>
            </button>
{/* USER PROFILE BUTTON - ALWAYS VISIBLE */}
<button 
  onClick={() => user && setShowProfileModal(!showProfileModal)}
  className={`profile-button ${user ? 'logged-in' : 'logged-out'}`}
  title={user ? 'View profile' : 'Login to view profile'}
>
  {profileData.profileImage && user ? (
    <img src={profileData.profileImage} alt={profileData.name} className="profile-image" />
  ) : (
    <User className="profile-icon" size={20} />
  )}
  <span className="profile-name">
    {user ? profileData.name || 'Profile' : 'Profile'}
  </span>
</button>


            {/* SIGN IN / SIGN UP BUTTONS - ONLY SHOW WHEN NOT LOGGED IN */}
            {!user && (
              <>
                <button onClick={handleSignIn} className="nav-button login-btn">
                  Sign In
                </button>
                <button onClick={handleCreateAccount} className="nav-button signup-btn">
                  Sign Up
                </button>
              </>
            )}
          </div>
        </div>
      </nav>

      {/* PROFILE MODAL */}
      {showProfileModal && user && (
        <div className="profile-modal-overlay" onClick={() => setShowProfileModal(false)}>
          <div 
            className="profile-modal"
            onClick={(e) => e.stopPropagation()}
          >
            {isFetchingProfile ? (
              <div className="profile-modal-loading">
                <div className="spinner"></div>
              </div>
            ) : (
              <>
                {!isEditing ? (
                  <div className="profile-modal-content">
                    <div className="profile-modal-header">
                      {profileData.profileImage ? (
                        <img src={profileData.profileImage} alt={profileData.name} className="profile-modal-image" />
                      ) : (
                        <div className="profile-modal-avatar">
                          <Users size={40} />
                        </div>
                      )}
                      <h3 className="profile-modal-name">{profileData.name || 'N/A'}</h3>
                      <p className="profile-modal-username">@{profileData.username || 'N/A'}</p>
                      <p className="profile-modal-email">{profileData.email || 'N/A'}</p>
                    </div>
                    <div className="profile-modal-buttons">
                      <button
                        onClick={() => setIsEditing(true)}
                        className="profile-modal-btn edit-btn"
                      >
                        <Edit2 size={16} />
                        Edit Profile
                      </button>
                      <button
                        onClick={handleLogout}
                        className="profile-modal-btn logout-btn"
                      >
                        <LogOut size={16} />
                        Logout
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="profile-modal-content">
                    <h3 className="profile-edit-title">Edit Profile</h3>
                    
                    <div className="profile-edit-image">
                      <label className="profile-edit-label">
                        {editData.profileImage ? (
                          <img src={editData.profileImage} alt="profile" className="profile-edit-img" />
                        ) : (
                          <div className="profile-edit-placeholder">
                            <Camera size={32} />
                          </div>
                        )}
                        <input
                          type="file"
                          onChange={handleProfileImageChange}
                          accept="image/*"
                          className="hidden"
                        />
                      </label>
                    </div>

                    <div className="profile-edit-fields">
                      <input
                        type="text"
                        name="name"
                        value={editData.name}
                        onChange={handleEditInputChange}
                        placeholder="Name"
                        className="profile-edit-input"
                      />
                      <input
                        type="text"
                        name="username"
                        value={editData.username}
                        onChange={handleEditInputChange}
                        placeholder="Username"
                        className="profile-edit-input"
                      />
                      <input
                        type="email"
                        name="email"
                        value={editData.email}
                        onChange={handleEditInputChange}
                        placeholder="Email"
                        className="profile-edit-input"
                      />
                    </div>

                    <div className="profile-edit-actions">
                      <button
                        onClick={() => setIsEditing(false)}
                        className="profile-modal-btn cancel-btn"
                      >
                        Cancel
                      </button>
                      <button
                        onClick={handleSaveProfile}
                        disabled={isLoading}
                        className="profile-modal-btn save-btn"
                      >
                        {isLoading ? (
                          <>
                            <div className="spinner-small"></div>
                            Saving...
                          </>
                        ) : (
                          <>
                            <Check size={16} />
                            Save
                          </>
                        )}
                      </button>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      )}

      <section className="hero-section">
        <motion.div
          className="sliding-text-container"
          variants={sliderVariants}
          initial="initial"
          animate="animate"
        >
          Raise Funds Pool Gifts Organize Events
        </motion.div>

        <div className="hero-wrapper">
          <motion.div
            className="hero-text-container"
            variants={textVariants}
            initial="initial"
            animate="animate"
          >
            <motion.h2 variants={textVariants} className="hero-subtitle">
              POOLING MADE EASY
            </motion.h2>
            <motion.h1 variants={textVariants} className="hero-title">
              NexFund
            </motion.h1>
            <motion.p variants={textVariants} className="hero-description">
              Raise funds, pool gifts, and organize donations with zero hassle.
            </motion.p>
            <motion.p variants={textVariants} className="description-highlight">
              Join thousands raising together.
            </motion.p>
            <motion.div variants={textVariants} className="hero-buttons">
              <motion.button
                className="btn btn-primary"
                onClick={handleSignIn}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                SIGN IN LOGIN
              </motion.button>
              <motion.button
                className="btn btn-secondary"
                onClick={handleCreateAccount}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                CREATE ACCOUNT
              </motion.button>
            </motion.div>
          </motion.div>

          <div>
            <div className="animation-layer">
              <div className="link2-icon">
                <Link2 className="link2-lucide" size={50} />
              </div>

              <div className="jar">
                <svg width="120" height="140" viewBox="0 0 120 140">
                  <rect className="jar-container" x="20" y="30" width="80" height="90" rx="5" />
                  <rect className="jar-container" x="15" y="20" width="90" height="15" rx="3" />
                </svg>
                <div className="jar-fill"></div>
              </div>

              <div className="checkmark">
                <svg width="60" height="60" viewBox="0 0 60 60">
                  <circle className="checkmark-circle" cx="30" cy="30" r="28" />
                  <path
                    className="checkmark-check"
                    d="M15 30 L25 40 L45 20"
                  />
                </svg>
              </div>

              {[1, 2, 3, 4, 5, 6].map((i) => (
                <div key={i} className="stick-figure" id={`figure${i}`}>
                  <svg width="60" height="80" viewBox="0 0 60 80">
                    <circle className="stick-head" cx="30" cy="15" r="10" />
                    <line className="stick-body" x1="30" y1="25" x2="30" y2="50" />
                    <line className="stick-body" x1="30" y1="35" x2="15" y2="45" />
                    <line className="stick-body" x1="30" y1="35" x2="45" y2="45" />
                    <line className="stick-body" x1="30" y1="50" x2="20" y2="70" />
                    <line className="stick-body" x1="30" y1="50" x2="40" y2="70" />
                  </svg>
                </div>
              ))}

              {[1, 2, 3, 4, 5, 6, 7].map((i) => (
                <div key={`coin${i}`} className={`coin coin${i}`}></div>
              ))}
            </div>
          </div>
        </div>

        <motion.div
          className="scroll-button"
          variants={scrollButtonVariants}
          animate="animate"
        >
          <span>Scroll to explore</span>
        </motion.div>
      </section>

      {/* STATS SECTION */}
      <section className="stats-section">
        <motion.h2
          className="section-title"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          Our Impact
        </motion.h2>
        <div className="stats-grid">
          <motion.div
            className="stat-card stat-events"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.1 }}
            viewport={{ once: true }}
          >
            <div className="stat-icon-container">
              <div className="stat-icon-wrapper stat-icon-events">
                <Calendar className="stat-icon-svg" size={48} />
              </div>
            </div>
            <div className="stat-number">{stats.events.toLocaleString()}</div>
            <p className="stat-label">Events Created</p>
          </motion.div>

          <motion.div
            className="stat-card stat-donations"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.2 }}
            viewport={{ once: true }}
          >
            <div className="stat-icon-container">
              <div className="stat-icon-wrapper stat-icon-donations">
                <Heart className="stat-icon-svg" size={48} />
              </div>
            </div>
            <div className="stat-number">{stats.donations.toLocaleString()}</div>
            <p className="stat-label">Donations Raised</p>
          </motion.div>

          <motion.div
            className="stat-card stat-giftpools"
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.3 }}
            viewport={{ once: true }}
          >
            <div className="stat-icon-container">
              <div className="stat-icon-wrapper stat-icon-giftpools">
                <Gift className="stat-icon-svg" size={48} />
              </div>
            </div>
            <div className="stat-number">{stats.giftPools.toLocaleString()}</div>
            <p className="stat-label">Gift Pools Created</p>
          </motion.div>
        </div>
      </section>

      {/* FEATURES SECTION */}
      <section className="features-section">
        <motion.h2
          className="section-title"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          Why Choose NexFund
        </motion.h2>
        <div className="features-grid">
          {features.map((feature, index) => {
            const FeatureIcon = feature.icon;
            return (
              <motion.div
                key={feature.id}
                className={`feature-card feature-${feature.color}`}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: index * 0.1 }}
                viewport={{ once: true }}
              >
                <div className="feature-icon-wrapper">
                  <FeatureIcon className="feature-icon" size={40} />
                </div>
                <h3 className="feature-title">{feature.title}</h3>
                <p className="feature-description">{feature.description}</p>
              </motion.div>
            );
          })}
        </div>
      </section>

      {/* REVIEWS SECTION */}
      <section className="reviews-section">
        <motion.h2
          className="section-title"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          What Users Love
        </motion.h2>

        {isLoadingFeedbacks ? (
          <div className="reviews-loading">
            <div className="spinner"></div>
          </div>
        ) : (
          <>
            <div className="reviews-grid">
              {displayedReviews.map((review, index) => {
                const reviewType = review.type || 'event';

                return (
                  <motion.div
                    key={`${review.id}-${index}`}
                    className={`review-card review-${reviewType}`}
                    initial={{ opacity: 0, y: 20, scale: 0.95 }}
                    whileInView={{ opacity: 1, y: 0, scale: 1 }}
                    transition={{ duration: 0.4, delay: index * 0.1 }}
                    viewport={{ once: false }}
                  >
                    <div className={`review-avatar avatar-${reviewType}`}>
                      {review.name.split(' ').map(n => n[0]).join('')}
                    </div>

                    {allUserFeedbacks.length > 0 && review.rating && (
                      <div className="review-rating">
                        <p className="review-author">{review.name}</p>
                        <div className="review-stars">
                          {[...Array(review.rating)].map((_, i) => (
                            <Star key={i} className="star-icon" size={14} />
                          ))}
                        </div>
                      </div>
                    )}

                    {allUserFeedbacks.length === 0 && (
                      <p className="review-author">{review.name}</p>
                    )}

                    <p className="review-text">{review.message || review.text}</p>
                    <p className="review-role">{review.role}</p>
                  </motion.div>
                );
              })}
            </div>

            {feedbackCount > 0 && (
              <div className="feedback-counter">
                <span className="feedback-badge">
                  {feedbackCount} total feedback{feedbackCount !== 1 ? 's' : ''}
                </span>
              </div>
            )}
          </>
        )}
      </section>

      {/* HAVE ANY QUERIES SECTION */}
      <section className="queries-section">
        <motion.div
          className="queries-container"
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          <h2>Have Any Queries?</h2>

          <form onSubmit={handleFormSubmit} className="query-form">
            <input
              type="text"
              name="name"
              placeholder="Your Name"
              value={formData.name}
              onChange={handleFormChange}
              required
            />
            <input
              type="email"
              name="email"
              placeholder="Your Email"
              value={formData.email}
              onChange={handleFormChange}
              required
            />
            <input
              type="tel"
              name="phone"
              placeholder="Phone (Optional)"
              value={formData.phone}
              onChange={handleFormChange}
            />

            <select
              name="subject"
              value={formData.subject}
              onChange={handleFormChange}
              required
            >
              <option value="">Select Subject</option>
              <option value="events">Questions About Events</option>
              <option value="donations">Help with Donations</option>
              <option value="giftpools">Gift Pool Assistance</option>
              <option value="general">General Query</option>
            </select>

            <textarea
              name="message"
              placeholder="Your Message"
              rows="5"
              value={formData.message}
              onChange={handleFormChange}
              required
            ></textarea>

            <button type="submit" className="btn btn-primary query-submit-btn">
              {formStatus === 'sending' ? 'Sending...' : 'Send Query'}
            </button>

            {formStatus === 'success' && (
              <p className="form-success">✓ Query sent successfully!</p>
            )}
            {formStatus === 'error' && (
              <p className="form-error">✗ Error sending query. Please try again.</p>
            )}
          </form>
        </motion.div>
      </section>

      {/* CTA SECTION */}
      <section className="cta-section">
        <motion.div
          className="cta-container"
          initial={{ opacity: 0, scale: 0.8 }}
          whileInView={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.6 }}
          viewport={{ once: true }}
        >
          <h2>Ready to Start Fundraising?</h2>
          <p>Create your first event, donation, or gift pool today</p>
          <motion.button
            className="btn btn-primary cta-button"
            onClick={handleCreateAccount}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            GET STARTED NOW
          </motion.button>
        </motion.div>
      </section>

      {/* Feedback Modal */}
      {showFeedbackModal && (
        <div className="feedback-modal-overlay">
          <div className="feedback-modal">
            <div className="feedback-modal-header">
              <h2>Share Your Feedback</h2>
              <button
                onClick={handleSkipFeedback}
                className="feedback-close-btn"
              >
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleFeedbackSubmit} className="feedback-form">
              <div>
                <label>Name *</label>
                <input
                  type="text"
                  name="name"
                  value={feedbackData.name}
                  onChange={handleFeedbackChange}
                  required
                  placeholder="Your name"
                  className="feedback-input"
                />
              </div>

              <div>
                <label>Type of Feedback *</label>
                <select
                  name="type"
                  value={feedbackData.type}
                  onChange={handleFeedbackChange}
                  required
                  className="feedback-input"
                >
                  <option value="event">Event</option>
                  <option value="donation">Donation</option>
                  <option value="giftpool">Gift Pool</option>
                </select>
              </div>

              <div>
                <label>Rating *</label>
                <div className="feedback-rating">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => handleFeedbackRating(star)}
                      className={`feedback-star ${feedbackData.rating >= star ? 'active' : ''}`}
                    >
                      <Star size={20} />
                    </button>
                  ))}
                  <span className="feedback-rating-text">{feedbackData.rating} / 5</span>
                </div>
              </div>

              <div>
                <label>Your Feedback *</label>
                <textarea
                  name="message"
                  value={feedbackData.message}
                  onChange={handleFeedbackChange}
                  required
                  rows="4"
                  placeholder="Tell us what you think..."
                  className="feedback-input"
                />
              </div>

              <div className="feedback-buttons">
                <button
                  type="button"
                  onClick={handleSkipFeedback}
                  className="feedback-btn skip-btn"
                >
                  Skip
                </button>
                <button
                  type="submit"
                  disabled={isSubmittingFeedback}
                  className="feedback-btn submit-btn"
                >
                  {isSubmittingFeedback ? (
                    <>
                      <div className="spinner-small"></div>
                      Submitting...
                    </>
                  ) : (
                    <>
                      <Send size={16} />
                      Submit
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
       <footer className="footer">
    <p>© {new Date().getFullYear()} NexFund. All rights reserved.</p>
  </footer>
    </div>
  );
};

export default Landing;
