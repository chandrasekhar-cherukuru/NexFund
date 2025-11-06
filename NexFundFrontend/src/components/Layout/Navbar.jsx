import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { Gift, Calendar, Heart, LogOut, User, X, Camera, Edit2, Check } from 'lucide-react';
import DarkModeToggle from '../DarkModeToggle';
import FeedbackModal from "../Feedback/FeedbackModal";
import toast from 'react-hot-toast';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [currentIconIndex, setCurrentIconIndex] = useState(0);
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [showFeedbackModal, setShowFeedbackModal] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isFetchingProfile, setIsFetchingProfile] = useState(false);
  const [isLogoutPending, setIsLogoutPending] = useState(false); // ✅ NEW
  
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

  const icons = [
    { 
      component: <Gift className="h-6 w-6" />, 
      label: 'NexFund',
      subtitle: 'Gift Pooling'
    },
    { 
      component: <Calendar className="h-6 w-6" />, 
      label: 'NexFund',
      subtitle: 'Events'
    },
    { 
      component: <Heart className="h-6 w-6" />, 
      label: 'NexFund',
      subtitle: 'Donations'
    }
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIconIndex((prevIndex) => (prevIndex + 1) % icons.length);
    }, 2000);
    return () => clearInterval(interval);
  }, [icons.length]);

  const fetchUserData = async () => {
    setIsFetchingProfile(true);
    try {
      let token = localStorage.getItem('token') || 
                 localStorage.getItem('jwt_token') || 
                 localStorage.getItem('auth_token') ||
                 localStorage.getItem('authToken');
      
      if (!token) {
        console.error('❌ No token found in localStorage');
        toast.error('Session expired. Please log in again.');
        setIsFetchingProfile(false);
        return;
      }

      const response = await fetch('http://localhost:8080/getProfile', {
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
      } else {
        console.error('❌ Failed to fetch profile:', response.status);
        toast.error('Failed to load profile');
      }
    } catch (error) {
      console.error('❌ Fetch error:', error.message);
      toast.error('Error loading profile');
    } finally {
      setIsFetchingProfile(false);
    }
  };

  // ✅ FIXED: Show feedback modal FIRST, then logout
  const handleLogout = () => {
    console.log('🔍 Logout clicked - showing feedback modal first');
    setShowFeedbackModal(true); // Show modal BEFORE logout
    setIsLogoutPending(true); // Mark that logout is pending
  };

  // ✅ FIXED: Only call logout AFTER feedback modal closes
  const handleFeedbackModalClose = () => {
    console.log('🔍 Feedback modal closed - performing logout now');
    setShowFeedbackModal(false);
    
    if (isLogoutPending) {
      toast.success('Logged out successfully');
      logout(); // Clear auth AFTER modal closes
      setIsLogoutPending(false);
      
      // Navigate after auth is cleared
      setTimeout(() => {
        navigate('/');
      }, 100);
    }
  };

  const handleProfileImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setEditData(prev => ({
          ...prev,
          profileImage: reader.result
        }));
        toast.success('Profile photo updated!');
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSaveProfile = async () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(editData.email)) {
      toast.error('Please enter a valid email address');
      return;
    }

    if (!editData.name.trim()) {
      toast.error('Name cannot be empty');
      return;
    }

    if (!editData.username.trim()) {
      toast.error('Username cannot be empty');
      return;
    }

    setIsLoading(true);

    try {
      let token = localStorage.getItem('token') || 
                 localStorage.getItem('jwt_token') || 
                 localStorage.getItem('auth_token') ||
                 localStorage.getItem('authToken');

      const response = await fetch('http://localhost:8080/updateProfile', {
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

      const data = await response.json();

      if (response.ok) {
        toast.success('Profile updated successfully! 🎉');
        setIsEditing(false);
        setProfileData({
          profileImage: data.profileImage || null,
          name: data.name || '',
          username: data.username || '',
          email: data.email || ''
        });
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      } else {
        toast.error(data.error || 'Failed to update profile');
        console.error('Update error:', data);
      }
    } catch (error) {
      console.error('Error updating profile:', error);
      toast.error('Error updating profile: ' + error.message);
    } finally {
      setIsLoading(false);
    }
  };

  const getInitials = () => {
    const name = editData.name || editData.username || 'User';
    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <>
      <nav className="bg-white dark:bg-gray-900 shadow-sm border-b border-gray-200 dark:border-gray-700 transition-colors duration-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            
            <Link 
              to="/dashboard" 
              className="flex items-center space-x-2 text-xl font-bold text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 transition-all duration-300 group"
              title={`NexFund - ${icons[currentIconIndex].subtitle}`}
            >
              <div className="transition-all duration-300 ease-in-out transform group-hover:scale-110">
                {icons[currentIconIndex].component}
              </div>
              <div className="flex flex-col">
                <span className="leading-tight">{icons[currentIconIndex].label}</span>
                <span className="text-xs text-gray-500 dark:text-gray-400 font-normal leading-tight">
                  {icons[currentIconIndex].subtitle}
                </span>
              </div>
            </Link>

            <div className="flex items-center space-x-2 flex-1 ml-8">
              <Link 
                to="/dashboard" 
                className="px-3 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
              >
                Dashboard
              </Link>
              <Link 
                to="/create" 
                className="px-3 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
              >
                Create Event
              </Link>
            </div>

            <div className="flex items-center space-x-4">
              <DarkModeToggle />
              
              {user ? (
                <div className="flex items-center space-x-3">
                  <button
                    onClick={() => {
                      setShowProfileModal(true);
                      setIsEditing(false);
                      fetchUserData();
                    }}
                    className="flex items-center space-x-2 hover:bg-gray-100 dark:hover:bg-gray-700 px-2 py-1 rounded-lg transition-colors cursor-pointer group"
                  >
                    <User className="h-5 w-5 text-gray-600 dark:text-gray-400 group-hover:text-blue-600 dark:group-hover:text-blue-400" />
                    <div className="flex flex-col text-left">
                      <span className="text-sm font-medium text-gray-700 dark:text-gray-300 group-hover:text-blue-600 dark:group-hover:text-blue-400">
                        Welcome, {user.name || user.username}!
                      </span>
                      {user.email && (
                        <span className="text-xs text-gray-500 dark:text-gray-400">{user.email}</span>
                      )}
                    </div>
                  </button>

                  <button
                    onClick={handleLogout}
                    className="flex items-center space-x-1 text-gray-600 dark:text-gray-400 hover:text-red-600 dark:hover:text-red-400 transition-colors duration-200 px-2 py-1 rounded-md hover:bg-red-50 dark:hover:bg-red-900/20"
                  >
                    <LogOut className="h-4 w-4" />
                    <span className="font-medium text-sm">Logout</span>
                  </button>
                </div>
              ) : (
                <Link
                  to="/"
                  className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition-colors duration-200 font-medium shadow-sm hover:shadow-md"
                >
                  Login
                </Link>
              )}
            </div>
          </div>
        </div>
      </nav>

      {showProfileModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-gray-800 rounded-xl shadow-2xl w-full max-w-md overflow-hidden">
            
            <div className="flex justify-between items-center p-6 border-b border-gray-200 dark:border-gray-700 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-700 dark:to-gray-700">
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white">Profile</h2>
              <button
                onClick={() => {
                  setShowProfileModal(false);
                  setIsEditing(false);
                }}
                className="p-1 hover:bg-white/50 dark:hover:bg-gray-600 rounded-lg transition-colors"
              >
                <X className="h-5 w-5 text-gray-600 dark:text-gray-400" />
              </button>
            </div>

            <div className="p-6 space-y-6">
              
              {isFetchingProfile && (
                <div className="flex items-center justify-center">
                  <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 dark:border-blue-400"></div>
                </div>
              )}

              {!isFetchingProfile && (
                <>
                  <div className="flex flex-col items-center space-y-4">
                    <div className="relative">
                      {editData.profileImage ? (
                        <img
                          src={editData.profileImage}
                          alt={editData.name}
                          className="h-24 w-24 rounded-full object-cover ring-4 ring-blue-500"
                        />
                      ) : (
                        <div className="h-24 w-24 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-white text-3xl font-bold ring-4 ring-blue-500">
                          {getInitials()}
                        </div>
                      )}
                      
                      {isEditing && (
                        <label className="absolute bottom-0 right-0 p-2 bg-blue-600 hover:bg-blue-700 text-white rounded-full shadow-lg cursor-pointer transition-colors">
                          <Camera className="h-4 w-4" />
                          <input
                            type="file"
                            accept="image/*"
                            onChange={handleProfileImageChange}
                            className="hidden"
                          />
                        </label>
                      )}
                    </div>
                    {isEditing && <p className="text-xs text-gray-600 dark:text-gray-400">Click camera to change photo</p>}
                  </div>

                  <div className="space-y-4">
                    
                    <div className={`${isEditing ? 'border-2 border-blue-500' : 'bg-gray-50 dark:bg-gray-700/50'} p-4 rounded-lg transition-all`}>
                      <label className="block text-xs font-semibold text-gray-600 dark:text-gray-400 mb-2">
                        Full Name
                      </label>
                      {isEditing ? (
                        <input
                          type="text"
                          value={editData.name}
                          onChange={(e) => setEditData(prev => ({ ...prev, name: e.target.value }))}
                          className="w-full px-3 py-2 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder="Enter your name"
                        />
                      ) : (
                        <p className="text-lg font-medium text-gray-900 dark:text-white">
                          {profileData.name || 'Not provided'}
                        </p>
                      )}
                    </div>

                    <div className={`${isEditing ? 'border-2 border-blue-500' : 'bg-gray-50 dark:bg-gray-700/50'} p-4 rounded-lg transition-all`}>
                      <label className="block text-xs font-semibold text-gray-600 dark:text-gray-400 mb-2">
                        Username
                      </label>
                      {isEditing ? (
                        <input
                          type="text"
                          value={editData.username}
                          onChange={(e) => setEditData(prev => ({ ...prev, username: e.target.value }))}
                          className="w-full px-3 py-2 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder="Enter your username"
                        />
                      ) : (
                        <p className="text-lg font-medium text-gray-900 dark:text-white">
                          {profileData.username || 'Not provided'}
                        </p>
                      )}
                    </div>

                    <div className={`${isEditing ? 'border-2 border-blue-500' : 'bg-gray-50 dark:bg-gray-700/50'} p-4 rounded-lg transition-all`}>
                      <label className="block text-xs font-semibold text-gray-600 dark:text-gray-400 mb-2">
                        Email
                      </label>
                      {isEditing ? (
                        <input
                          type="email"
                          value={editData.email}
                          onChange={(e) => setEditData(prev => ({ ...prev, email: e.target.value }))}
                          className="w-full px-3 py-2 bg-white dark:bg-gray-700 text-gray-900 dark:text-white border border-gray-300 dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          placeholder="Enter your email"
                        />
                      ) : (
                        <p className="text-lg font-medium text-gray-900 dark:text-white break-all">
                          {profileData.email || 'Not available'}
                        </p>
                      )}
                    </div>
                  </div>
                </>
              )}
            </div>

            <div className="flex gap-3 p-6 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-700/50">
              {!isEditing ? (
                <>
                  <button
                    onClick={() => {
                      setShowProfileModal(false);
                      setIsEditing(false);
                    }}
                    className="flex-1 px-4 py-2 bg-gray-300 dark:bg-gray-600 text-gray-900 dark:text-white rounded-lg hover:bg-gray-400 dark:hover:bg-gray-500 transition-colors font-medium"
                  >
                    Close
                  </button>
                  <button
                    onClick={() => setIsEditing(true)}
                    className="flex-1 flex items-center justify-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors font-medium"
                  >
                    <Edit2 className="h-4 w-4" />
                    <span>Edit Profile</span>
                  </button>
                </>
              ) : (
                <>
                  <button
                    onClick={() => setIsEditing(false)}
                    className="flex-1 px-4 py-2 bg-gray-300 dark:bg-gray-600 text-gray-900 dark:text-white rounded-lg hover:bg-gray-400 dark:hover:bg-gray-500 transition-colors font-medium disabled:opacity-50"
                    disabled={isLoading}
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleSaveProfile}
                    className="flex-1 flex items-center justify-center space-x-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors font-medium disabled:opacity-50"
                    disabled={isLoading}
                  >
                    {isLoading ? (
                      <>
                        <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                        <span>Saving...</span>
                      </>
                    ) : (
                      <>
                        <Check className="h-4 w-4" />
                        <span>Save Changes</span>
                      </>
                    )}
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      )}

      {/* ✅ FEEDBACK MODAL - Shows on logout BEFORE auth is cleared */}
      <FeedbackModal 
        isOpen={showFeedbackModal} 
        onClose={handleFeedbackModalClose}
      />
    </>
  );
};

export default Navbar;
